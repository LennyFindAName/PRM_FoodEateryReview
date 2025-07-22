using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using BusinessObjects.Models;
using Microsoft.EntityFrameworkCore;
using System.Threading.Tasks;
using System;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using BusinessObjects.RequestModels;

namespace EateryReviewWebsiteBE.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AppNotificationController : ControllerBase
    {
        private readonly EateryReviewDbContext _context;

        public AppNotificationController(EateryReviewDbContext context)
        {
            _context = context;
        }

        // 1. Log/register FCM token
        [HttpPost("register-token")]
        public async Task<IActionResult> RegisterToken([FromBody] RegisterTokenRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.Token) || request.UserId <= 0)
                return BadRequest("Invalid token or userId.");

            var existing = await _context.FcmTokens
                .FirstOrDefaultAsync(t => t.UserId == request.UserId && t.Token == request.Token);

            if (existing == null)
            {
                var token = new FcmToken
                {
                    UserId = request.UserId,
                    Token = request.Token,
                    CreatedAt = DateTime.UtcNow
                };
                _context.FcmTokens.Add(token);
                await _context.SaveChangesAsync();
            }

            return Ok("Token registered.");
        }

        [HttpGet("getNotifBy/{userId}")]
        public async Task<IActionResult> GetNotifBy(int userId)
        {
            var notifs = _context.Notifications
                .Where(n => n.UserId == userId)
                .OrderByDescending(n => n.Timestamp)
                .ToList();

            return Ok(notifs);
        }

        [HttpDelete("deleteNotif/{id}")]
        public async Task<IActionResult> DeleteNotif(int id)
        {
            var notif = await _context.Notifications.FindAsync(id);
            if (notif == null)
                return NotFound("Notification not found.");
            _context.Notifications.Remove(notif);
            await _context.SaveChangesAsync();
            return Ok();
        }
    }
}