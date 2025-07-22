using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class PaidOption
{
    public int Id { get; set; }

    public int Amount { get; set; }

    public int Days { get; set; }

    public virtual ICollection<Blog> Blogs { get; set; } = new List<Blog>();
}
