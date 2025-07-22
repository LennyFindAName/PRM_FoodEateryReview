using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class FcmToken
{
    public int Id { get; set; }

    public int UserId { get; set; }

    public string Token { get; set; } = null!;

    public DateTime? CreatedAt { get; set; }

    public virtual User User { get; set; } = null!;
}
