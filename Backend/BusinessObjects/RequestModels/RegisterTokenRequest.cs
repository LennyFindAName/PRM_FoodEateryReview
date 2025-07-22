using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.RequestModels
{
    public class RegisterTokenRequest
    {
        [Required]
        public int UserId { get; set; }
        [Required]
        public string Token { get; set; }
    }
}
