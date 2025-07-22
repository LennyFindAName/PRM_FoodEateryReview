using Microsoft.AspNetCore.Mvc;
using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;
using System.Collections.Generic;
using EateryReviewWebsiteFE.Models;
using Microsoft.AspNetCore.WebUtilities;
using BusinessObjects.ModerationModels;
using Microsoft.Extensions.Configuration;
using BusinessObjects.Models;
using BusinessObjects.ModerationModels.Blog;
using BusinessObjects.ModerationModels.User;

namespace EateryReviewWebsiteFE.Controllers
{
    public class ModerationController : Controller
    {
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly string _apiBaseBEUrl;

        public ModerationController(IHttpClientFactory httpClientFactory, IConfiguration configuration)
        {
            _httpClientFactory = httpClientFactory;
            _apiBaseBEUrl = configuration["ApiBaseBEUrl"] ?? throw new Exception("Backend API not found.");
        }

        public async Task<IActionResult> Review(string title, string username, string dateFrom, string dateTo, int? status, int page = 1, int pageSize = 10)
        {
            var client = _httpClientFactory.CreateClient();
            var query = new Dictionary<string, string>
            {
                ["page"] = page.ToString(),
                ["pageSize"] = pageSize.ToString()
            };
            if (!string.IsNullOrWhiteSpace(title)) query["title"] = title;
            if (!string.IsNullOrWhiteSpace(username)) query["username"] = username;
            if (!string.IsNullOrWhiteSpace(dateFrom)) query["dateFrom"] = dateFrom;
            if (!string.IsNullOrWhiteSpace(dateTo)) query["dateTo"] = dateTo;
            if (status.HasValue) query["status"] = status.Value.ToString();

            var url = QueryHelpers.AddQueryString($"{_apiBaseBEUrl}/moderation/get-blogs", query);
            var response = await client.GetAsync(url);
            if (!response.IsSuccessStatusCode)
            {
                throw new Exception("Info error from moderation review backend.");
            }

            var model = await response.Content.ReadFromJsonAsync<BlogModerationViewModel>();
            return View(model);
        }

        // Blog Moderation Actions

        //[HttpGet]
        //public async Task<IActionResult> GetBlogs(string title, string username, string dateFrom, string dateTo, int? status, int page = 1, int pageSize = 10)
        //{
        //    var client = _httpClientFactory.CreateClient();
        //    var query = new Dictionary<string, string>
        //    {
        //        ["page"] = page.ToString(),
        //        ["pageSize"] = pageSize.ToString()
        //    };
        //    if (!string.IsNullOrWhiteSpace(title)) query["title"] = title;
        //    if (!string.IsNullOrWhiteSpace(username)) query["username"] = username;
        //    if (!string.IsNullOrWhiteSpace(dateFrom)) query["dateFrom"] = dateFrom;
        //    if (!string.IsNullOrWhiteSpace(dateTo)) query["dateTo"] = dateTo;
        //    if (status.HasValue) query["status"] = status.Value.ToString();

        //    var url = QueryHelpers.AddQueryString($"{_apiBaseBEUrl}/moderation/get-blogs", query);
        //    var response = await client.GetAsync(url);
        //    if (!response.IsSuccessStatusCode)
        //        return StatusCode((int)response.StatusCode, await response.Content.ReadAsStringAsync());

        //    var result = await response.Content.ReadFromJsonAsync<object>();
        //    return Json(result);
        //}

        [HttpPost]
        public async Task<IActionResult> ReviewBlog(int blogId, [FromBody] ReviewRequestModel reviewRequest)
        {
            var client = _httpClientFactory.CreateClient();
            var response = await client.PostAsJsonAsync($"{_apiBaseBEUrl}/moderation/review/{blogId}", reviewRequest);
            if (!response.IsSuccessStatusCode)
                return StatusCode((int)response.StatusCode, await response.Content.ReadAsStringAsync());

            var result = await response.Content.ReadFromJsonAsync<object>();
            return Json(result);
        }

        [HttpPost]
        public async Task<IActionResult> AssessByAI(int blogId)
        {
            var client = _httpClientFactory.CreateClient();
            var response = await client.PostAsync($"{_apiBaseBEUrl}/moderation/assess-by-ai/{blogId}", null);
            if (!response.IsSuccessStatusCode)
                return StatusCode((int)response.StatusCode, await response.Content.ReadAsStringAsync());

            var result = await response.Content.ReadFromJsonAsync<object>();
            return Json(result);
        }

        public async Task<IActionResult> ManageUsers(string username, string email, int? status, int page = 1, int pageSize = 10)
        {
            var client = _httpClientFactory.CreateClient();
            var query = new Dictionary<string, string>
            {
                ["page"] = page.ToString(),
                ["pageSize"] = pageSize.ToString()
            };
            if (!string.IsNullOrWhiteSpace(username)) query["username"] = username;
            if (!string.IsNullOrWhiteSpace(email)) query["email"] = email;
            if (status.HasValue) query["status"] = status.Value.ToString();

            var url = QueryHelpers.AddQueryString($"{_apiBaseBEUrl}/moderation/get-users", query);
            var response = await client.GetAsync(url);
            if (!response.IsSuccessStatusCode)
                throw new Exception("Error fetching users from backend.");

            var model = await response.Content.ReadFromJsonAsync<UserModerationViewModel>();
            return View(model);
        }

        [HttpPost]
        public async Task<IActionResult> ReviewUser(int userId, [FromBody] ReviewRequestModel reviewRequest)
        {
            var client = _httpClientFactory.CreateClient();
            var response = await client.PostAsJsonAsync(
                $"{_apiBaseBEUrl}/moderation/review-user/{userId}", reviewRequest);

            if (!response.IsSuccessStatusCode)
                return StatusCode((int)response.StatusCode, await response.Content.ReadAsStringAsync());

            var result = await response.Content.ReadFromJsonAsync<object>();
            return Json(result);
        }
    }
}
