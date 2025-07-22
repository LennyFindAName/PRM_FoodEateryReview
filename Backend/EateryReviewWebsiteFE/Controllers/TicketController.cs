using Microsoft.AspNetCore.Mvc;

namespace EateryReviewWebsiteFE.Controllers
{
    public class TicketController : Controller
    {
        public IActionResult Index()
        {
            return View(); 
        }
    }
}
