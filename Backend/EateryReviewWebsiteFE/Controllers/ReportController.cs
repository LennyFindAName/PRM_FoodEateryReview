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
    public class ReportController : Controller
    {
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly string _apiBaseBEUrl;

        public ReportController(IHttpClientFactory httpClientFactory, IConfiguration configuration)
        {
            _httpClientFactory = httpClientFactory;
            _apiBaseBEUrl = configuration["ApiBaseBEUrl"] ?? throw new Exception("Backend API not found.");
        }

        [HttpGet]
        public async Task<IActionResult> GetReports(int blogId)
        {
            var client = _httpClientFactory.CreateClient();
            var response = await client.GetAsync($"{_apiBaseBEUrl}/report/GetReports?blogId={blogId}");
            if (!response.IsSuccessStatusCode)
            {
                throw new Exception("Error fetching report reasons from backend.");
            }
            var reports = await response.Content.ReadFromJsonAsync<List<ReportViewModel>>();
            return Json(reports);
        }
    }
}
