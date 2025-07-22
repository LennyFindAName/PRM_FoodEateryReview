using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using BusinessObjects.Models;

namespace Services.Helper
{
    public class WalletHelper
    {
        private readonly EateryReviewDbContext _context;

        public WalletHelper(EateryReviewDbContext context)
        {
            _context = context;
        }

        public async Task ChangeBalanceAsync(int userId, int paidOptionId, int blogId)
        {
            try
            {
                var user = _context.Users.FirstOrDefault(u => u.UserId == userId);
                var paidOption = _context.PaidOptions.FirstOrDefault(po => po.Id == paidOptionId);
                var blog = _context.Blogs.FirstOrDefault(b => b.BlogId == blogId);

                if (blog == null) throw new Exception();
                if (paidOption == null) throw new Exception();
                if (user == null) throw new Exception();
                else 
                {
                    user.WalletBalance -= paidOption.Amount;
                    blog.PaidExpirationDate = DateTime.Now.AddDays(paidOption.Days);
                    await _context.SaveChangesAsync();
                }
            } catch (Exception ex)
            {
                throw new Exception("Error retrieving user from database.", ex);
            }

        }
    }
}
