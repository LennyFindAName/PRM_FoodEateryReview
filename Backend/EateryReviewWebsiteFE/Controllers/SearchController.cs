using Microsoft.AspNetCore.Mvc;

namespace EateryReviewWebsiteFE.Controllers
{
    public class SearchController : Controller
    {
        public IActionResult Index(string query)
        {
            // Pass the search query to the view
            ViewBag.SearchQuery = query ?? "";
            return View();
        }
    }
}
