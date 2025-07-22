﻿using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.Models
{
    public class EditModeratorModel
    {
        public int UserId { get; set; }

        [Required]
        public string Username { get; set; }

        [Required]
        public string DisplayName { get; set; }

        [Required, EmailAddress]
        public string UserEmail { get; set; }
    }
}
