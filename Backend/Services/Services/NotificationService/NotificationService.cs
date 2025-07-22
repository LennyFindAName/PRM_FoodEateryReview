using BusinessObjects.Enums;
using BusinessObjects.Models;
using Google.Apis.Auth.OAuth2;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace Services.Services.NotificationService
{
    public static class NotificationService
    {
        private readonly static string _serviceAccountJsonPath =
            Path.Combine(AppContext.BaseDirectory, "Services", "androidnoti-62841-firebase-adminsdk-fbsvc-7f7c37926e.json");
        public static async Task SendAsync(EateryReviewDbContext context, int userId, string message, MessageType messageType)
        {
            if (userId <= 0 || string.IsNullOrWhiteSpace(message))
                return;

            string title = messageType switch
            {
                MessageType.Success => "Thành công",
                MessageType.Failure => "Thất bại",
                MessageType.Info => "Thông báo",
                _ => "Thông báo không xác định"
            };

            var notification = new Notification
            {
                UserId = userId,
                Title = title,
                Message = message,
                IsRead = false,
                Timestamp = DateTime.Now
            };

            context.Notifications.Add(notification);
            await context.SaveChangesAsync();

            // Sending FCM notif after sending normal notif to web.
            var fcmTokens = await context.FcmTokens
                .Where(t => t.UserId == userId)
                .Select(t => t.Token)
                .ToListAsync();
            if (fcmTokens.Any())
            {
                await SendFcmV1NotificationAsync(context, fcmTokens, title, message).ConfigureAwait(false);
            }
        }

        public static async Task SendFcmV1NotificationAsync(
            EateryReviewDbContext context,
            IEnumerable<string> fcmTokens,
            string title,
            string body)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(_serviceAccountJsonPath) || !File.Exists(_serviceAccountJsonPath))
                    throw new ArgumentException("Service account JSON path is invalid.", nameof(_serviceAccountJsonPath));
                if (fcmTokens == null || !fcmTokens.Any())
                    throw new ArgumentException("FCM tokens are required.", nameof(fcmTokens));

                var credential = GoogleCredential.FromFile(_serviceAccountJsonPath)
                    .CreateScoped("https://www.googleapis.com/auth/firebase.messaging");
                var accessToken = await credential.UnderlyingCredential.GetAccessTokenForRequestAsync();

                using var client = new HttpClient();
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", accessToken);

                var json = File.ReadAllText(_serviceAccountJsonPath);
                using var doc = JsonDocument.Parse(json);
                string projectId = doc.RootElement.GetProperty("project_id").GetString();

                var url = $"https://fcm.googleapis.com/v1/projects/{projectId}/messages:send";

                foreach (var token in fcmTokens)
                {
                    var message = new
                    {
                        message = new
                        {
                            token,
                            notification = new
                            {
                                title,
                                body
                            }
                        }
                    };

                    try
                    {
                        var response = await client.PostAsJsonAsync(url, message);
                        if (!response.IsSuccessStatusCode)
                        {
                            var errorContent = await response.Content.ReadAsStringAsync();
                            Console.Error.WriteLine($"Failed to send to token {token}: {response.StatusCode} - {errorContent}");

                            // Remove unregistered tokens
                            if (errorContent.Contains("\"errorCode\": \"UNREGISTERED\""))
                            {
                                var entity = await context.FcmTokens.FirstOrDefaultAsync(t => t.Token == token);
                                if (entity != null)
                                {
                                    context.FcmTokens.Remove(entity);
                                    await context.SaveChangesAsync();
                                }
                                Console.Error.WriteLine($"Removed unregistered FCM token: {token}");
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Console.Error.WriteLine($"Exception sending to token {token}: {ex}");
                    }
                }
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine($"[NotificationService] Error sending FCM notification: {ex}");
            }
        }

        // Add this helper method somewhere in your NotificationService class
        private static async Task RemoveFcmTokenAsync(string token)
        {
            // You need a way to get your DbContext here. 
            // For static methods, you might need to refactor to inject the context, or pass it as a parameter.
            using var context = new EateryReviewDbContext(); // Adjust this to your DI setup
            var entity = await context.FcmTokens.FirstOrDefaultAsync(t => t.Token == token);
            if (entity != null)
            {
                context.FcmTokens.Remove(entity);
                await context.SaveChangesAsync();
            }
        }
    }
}
