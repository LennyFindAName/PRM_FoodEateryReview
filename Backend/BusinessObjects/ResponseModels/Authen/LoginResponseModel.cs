﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.ResponseModels.Authen
{
    public class LoginResponseModel
    {
        public string AccessToken { get; set; }
        public string Username { get; set; }
        public int UserId { get; set; }
        public int RoleId { get; set; }
    }
}
