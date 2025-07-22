using BusinessObjects.Models;
using BusinessObjects.ModerationModels.Blog;
using Microsoft.AspNetCore.Mvc;

namespace EateryReviewWebsiteFE.Controllers
{
    public class BlogController : Controller
    {
        private readonly IConfiguration _configuration;
        private readonly EateryReviewDbContext _context;
        public BlogController(IConfiguration configuration, EateryReviewDbContext _context)
        {
            _configuration = configuration;
            this._context = _context;
        }
        public IActionResult Index()
        {
            return View();
        }
        public IActionResult Details()
        {
            ViewBag.GoogleMapsApiKey = _configuration["GoogleMapAPI:Key"];
            return View();
        }

        public IActionResult Create()
        {

            // Get paid options from database and create a simple list
            var paidOptionsFromDb = _context.PaidOptions.ToList();

            // Create a list that includes the "None" option plus database options
            var paidOptionsForView = new List<object>();

            // Add "None" option first
            paidOptionsForView.Add(new
            {
                Id = 0,
                Amount = 0,
                Days = 0,
                DisplayText = "Không trả phí"
            });

            // Add database options
            foreach (var option in paidOptionsFromDb)
            {
                paidOptionsForView.Add(new
                {
                    Id = option.Id,
                    Amount = option.Amount,
                    Days = option.Days,
                    DisplayText = $"{option.Amount} VND/ {option.Days} ngày"
                });
            }

            ViewBag.PaidBlogOptions = paidOptionsForView;
            ViewBag.GoogleMapsApiKey = _configuration["GoogleMapAPI:Key"];
            return View();
        }
    }
}
