﻿using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class EateryReviewDbContext : DbContext
{
    public EateryReviewDbContext()
    {
    }

    public EateryReviewDbContext(DbContextOptions<EateryReviewDbContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Blog> Blogs { get; set; }

    public virtual DbSet<BlogFoodType> BlogFoodTypes { get; set; }

    public virtual DbSet<BlogImage> BlogImages { get; set; }

    public virtual DbSet<BlogLike> BlogLikes { get; set; }

    public virtual DbSet<BlogMealType> BlogMealTypes { get; set; }

    public virtual DbSet<BlogPriceRange> BlogPriceRanges { get; set; }

    public virtual DbSet<Bookmark> Bookmarks { get; set; }

    public virtual DbSet<Comment> Comments { get; set; }

    public virtual DbSet<CommentLike> CommentLikes { get; set; }

    public virtual DbSet<FcmToken> FcmTokens { get; set; }

    public virtual DbSet<Notification> Notifications { get; set; }

    public virtual DbSet<PaidOption> PaidOptions { get; set; }

    public virtual DbSet<Reply> Replies { get; set; }

    public virtual DbSet<ReplyLike> ReplyLikes { get; set; }

    public virtual DbSet<Report> Reports { get; set; }

    public virtual DbSet<ReportReason> ReportReasons { get; set; }

    public virtual DbSet<Ticket> Tickets { get; set; }

    public virtual DbSet<TicketType> TicketTypes { get; set; }

    public virtual DbSet<User> Users { get; set; }

    public virtual DbSet<WalletTransaction> WalletTransactions { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        if (!optionsBuilder.IsConfigured)
        {
            var ConnectionString = new ConfigurationBuilder().AddJsonFile("appsettings.json").Build().GetConnectionString("DefaultConnection");
            optionsBuilder.UseSqlServer(ConnectionString);
        }
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Blog>(entity =>
        {
            entity.HasKey(e => e.BlogId).HasName("PK__blog__2975AA282D5ABD33");

            entity.ToTable("blog");

            entity.Property(e => e.BlogId).HasColumnName("blog_id");
            entity.Property(e => e.BlogBillImage).HasColumnName("blog_bill_image");
            entity.Property(e => e.BlogContent).HasColumnName("blog_content");
            entity.Property(e => e.BlogDate).HasColumnName("blog_date");
            entity.Property(e => e.BlogLike).HasColumnName("blog_like");
            entity.Property(e => e.BlogRate).HasColumnName("blog_rate");
            entity.Property(e => e.BlogStatus).HasColumnName("blog_status");
            entity.Property(e => e.BlogTitle)
                .HasMaxLength(100)
                .HasColumnName("blog_title");
            entity.Property(e => e.EateryAddressDetail)
                .HasMaxLength(100)
                .HasColumnName("eatery_address_detail");
            entity.Property(e => e.EateryId).HasColumnName("eatery_id");
            entity.Property(e => e.EateryLocationDetail)
                .HasMaxLength(100)
                .HasColumnName("eatery_location_detail");
            entity.Property(e => e.EateryNameDetail)
                .HasMaxLength(100)
                .HasColumnName("eatery_name_detail");
            entity.Property(e => e.EnvironmentRate).HasColumnName("environment_rate");
            entity.Property(e => e.FoodQualityRate).HasColumnName("food_quality_rate");
            entity.Property(e => e.HygieneRate).HasColumnName("hygiene_rate");
            entity.Property(e => e.Opinion).HasColumnName("opinion");
            entity.Property(e => e.PaidExpirationDate)
                .HasColumnType("datetime")
                .HasColumnName("paid_expiration_date");
            entity.Property(e => e.PaidOptionId).HasColumnName("paid_option_id");
            entity.Property(e => e.PricingRate).HasColumnName("pricing_rate");
            entity.Property(e => e.ServiceRate).HasColumnName("service_rate");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.PaidOption).WithMany(p => p.Blogs)
                .HasForeignKey(d => d.PaidOptionId)
                .HasConstraintName("blog_paid_option_id_fk");

            entity.HasOne(d => d.User).WithMany(p => p.Blogs)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("FK__blog__user_id__398D8EEE");
        });

        modelBuilder.Entity<BlogFoodType>(entity =>
        {
            entity.ToTable("blog_food_type");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.BlogId).HasColumnName("blog_id");
            entity.Property(e => e.FoodTypeName)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("food_type_name");

            entity.HasOne(d => d.Blog).WithMany(p => p.BlogFoodTypes)
                .HasForeignKey(d => d.BlogId)
                .HasConstraintName("FK__blog_food__blog___440B1D61");
        });

        modelBuilder.Entity<BlogImage>(entity =>
        {
            entity.HasKey(e => e.ImageId).HasName("PK__blog_ima__DC9AC955508EC548");

            entity.ToTable("blog_image");

            entity.Property(e => e.ImageId).HasColumnName("image_id");
            entity.Property(e => e.BlogId).HasColumnName("blog_id");
            entity.Property(e => e.BlogImage1).HasColumnName("blog_image");

            entity.HasOne(d => d.Blog).WithMany(p => p.BlogImages)
                .HasForeignKey(d => d.BlogId)
                .HasConstraintName("FK__blog_imag__blog___403A8C7D");
        });

        modelBuilder.Entity<BlogLike>(entity =>
        {
            entity.HasKey(e => e.LikeId).HasName("PK__blog_lik__992C7930EE16FEA6");

            entity.ToTable("blog_like");

            entity.Property(e => e.LikeId).HasColumnName("like_id");
            entity.Property(e => e.BlogId).HasColumnName("blog_id");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.Blog).WithMany(p => p.BlogLikes)
                .HasForeignKey(d => d.BlogId)
                .HasConstraintName("FK__blog_like__blog___571DF1D5");

            entity.HasOne(d => d.User).WithMany(p => p.BlogLikes)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("FK__blog_like__user___1C873BEC");
        });

        modelBuilder.Entity<BlogMealType>(entity =>
        {
            entity.ToTable("blog_meal_type");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.BlogId).HasColumnName("blog_id");
            entity.Property(e => e.MealTypeName)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("meal_type_name");

            entity.HasOne(d => d.Blog).WithMany(p => p.BlogMealTypes)
                .HasForeignKey(d => d.BlogId)
                .HasConstraintName("FK__blog_meal__blog___4222D4EF");
        });

        modelBuilder.Entity<BlogPriceRange>(entity =>
        {
            entity.ToTable("blog_price_range");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.BlogId).HasColumnName("blog_id");
            entity.Property(e => e.PriceRangeValue)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("price_range_value");

            entity.HasOne(d => d.Blog).WithMany(p => p.BlogPriceRanges)
                .HasForeignKey(d => d.BlogId)
                .HasConstraintName("FK__blog_pric__blog___45F365D3");
        });

        modelBuilder.Entity<Bookmark>(entity =>
        {
            entity.HasKey(e => e.BookmarkId).HasName("PK__bookmark__D9C6580201001867");

            entity.ToTable("bookmark");

            entity.Property(e => e.BookmarkId).HasColumnName("bookmark_id");
            entity.Property(e => e.BlogId).HasColumnName("blog_id");
            entity.Property(e => e.BookmarkByUserId).HasColumnName("bookmark_by_user_id");

            entity.HasOne(d => d.Blog).WithMany(p => p.Bookmarks)
                .HasForeignKey(d => d.BlogId)
                .HasConstraintName("FK__bookmark__blog_i__3D5E1FD2");

            entity.HasOne(d => d.BookmarkByUser).WithMany(p => p.Bookmarks)
                .HasForeignKey(d => d.BookmarkByUserId)
                .HasConstraintName("FK__bookmark__bookma__1EA48E88");
        });

        modelBuilder.Entity<Comment>(entity =>
        {
            entity.HasKey(e => e.CommentId).HasName("PK__comment__E7957687774392BC");

            entity.ToTable("comment");

            entity.Property(e => e.CommentId).HasColumnName("comment_id");
            entity.Property(e => e.BlogId).HasColumnName("blog_id");
            entity.Property(e => e.CommentDate)
                .HasColumnType("datetime")
                .HasColumnName("comment_date");
            entity.Property(e => e.CommentLike).HasColumnName("comment_like");
            entity.Property(e => e.CommentStatus).HasColumnName("comment_status");
            entity.Property(e => e.Content).HasColumnName("content");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.Blog).WithMany(p => p.Comments)
                .HasForeignKey(d => d.BlogId)
                .HasConstraintName("FK__comment__blog_id__49C3F6B7");

            entity.HasOne(d => d.User).WithMany(p => p.Comments)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("FK__comment__user_id__1F98B2C1");
        });

        modelBuilder.Entity<CommentLike>(entity =>
        {
            entity.HasKey(e => e.LikeId).HasName("PK__comment___992C79300BA7A17A");

            entity.ToTable("comment_like");

            entity.Property(e => e.LikeId).HasColumnName("like_id");
            entity.Property(e => e.CommentId).HasColumnName("comment_id");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.Comment).WithMany(p => p.CommentLikes)
                .HasForeignKey(d => d.CommentId)
                .HasConstraintName("FK__comment_l__comme__60A75C0F");

            entity.HasOne(d => d.User).WithMany(p => p.CommentLikes)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("FK__comment_l__user___2180FB33");
        });

        modelBuilder.Entity<FcmToken>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__fcm_toke__3213E83FAF48759D");

            entity.ToTable("fcm_token");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("(getdate())")
                .HasColumnType("datetime")
                .HasColumnName("created_at");
            entity.Property(e => e.Token)
                .HasMaxLength(512)
                .HasColumnName("token");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.User).WithMany(p => p.FcmTokens)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("fk_user_id");
        });

        modelBuilder.Entity<Notification>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__notifica__3213E83F03AC65B7");

            entity.ToTable("notifications");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.IsRead).HasColumnName("is_read");
            entity.Property(e => e.Message).HasColumnName("message");
            entity.Property(e => e.Timestamp)
                .HasDefaultValueSql("(getdate())")
                .HasColumnName("timestamp");
            entity.Property(e => e.Title)
                .HasMaxLength(255)
                .HasColumnName("title");
            entity.Property(e => e.UserId).HasColumnName("user_id");
        });

        modelBuilder.Entity<PaidOption>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("paid_option_pk");

            entity.ToTable("paid_option");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Amount).HasColumnName("amount");
            entity.Property(e => e.Days).HasColumnName("days");
        });

        modelBuilder.Entity<Reply>(entity =>
        {
            entity.HasKey(e => e.ReplyId).HasName("PK__reply__EE405698E59A1926");

            entity.ToTable("reply");

            entity.Property(e => e.ReplyId).HasColumnName("reply_id");
            entity.Property(e => e.CommentId).HasColumnName("comment_id");
            entity.Property(e => e.Content)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("content");
            entity.Property(e => e.ReplyDate)
                .HasColumnType("datetime")
                .HasColumnName("reply_date");
            entity.Property(e => e.ReplyLike).HasColumnName("reply_like");
            entity.Property(e => e.ReplyStatus).HasColumnName("reply_status");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.Comment).WithMany(p => p.Replies)
                .HasForeignKey(d => d.CommentId)
                .HasConstraintName("FK__reply__comment_i__66603565");

            entity.HasOne(d => d.User).WithMany(p => p.Replies)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("FK_reply_user");
        });

        modelBuilder.Entity<ReplyLike>(entity =>
        {
            entity.HasKey(e => e.LikeId).HasName("PK__reply_li__992C7930ED9CCF1E");

            entity.ToTable("reply_like");

            entity.Property(e => e.LikeId).HasColumnName("like_id");
            entity.Property(e => e.ReplyId).HasColumnName("reply_id");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.Reply).WithMany(p => p.ReplyLikes)
                .HasForeignKey(d => d.ReplyId)
                .HasConstraintName("FK__reply_lik__reply__6B24EA82");

            entity.HasOne(d => d.User).WithMany(p => p.ReplyLikes)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("FK__reply_lik__user___245D67DE");
        });

        modelBuilder.Entity<Report>(entity =>
        {
            entity.HasKey(e => e.ReportId).HasName("PK__report__779B7C58F089470A");

            entity.ToTable("report");

            entity.Property(e => e.ReportId).HasColumnName("report_id");
            entity.Property(e => e.BlogId).HasColumnName("blog_id");
            entity.Property(e => e.ReportContent)
                .HasMaxLength(250)
                .HasColumnName("report_content");
            entity.Property(e => e.ReportReasonId).HasColumnName("report_reason_id");
            entity.Property(e => e.ReportStatus).HasColumnName("report_status");
            entity.Property(e => e.ReportTime)
                .HasColumnType("datetime")
                .HasColumnName("report_time");
            entity.Property(e => e.ReporterId).HasColumnName("reporter_id");

            entity.HasOne(d => d.Blog).WithMany(p => p.Reports)
                .HasForeignKey(d => d.BlogId)
                .HasConstraintName("FK_report_blog");

            entity.HasOne(d => d.ReportReason).WithMany(p => p.Reports)
                .HasForeignKey(d => d.ReportReasonId)
                .HasConstraintName("FK_report_reason");

            entity.HasOne(d => d.Reporter).WithMany(p => p.Reports)
                .HasForeignKey(d => d.ReporterId)
                .HasConstraintName("FK_report_user");
        });

        modelBuilder.Entity<ReportReason>(entity =>
        {
            entity.HasKey(e => e.ReasonId).HasName("PK__report_r__846BB5540730D960");

            entity.ToTable("report_reason");

            entity.Property(e => e.ReasonId).HasColumnName("reason_id");
            entity.Property(e => e.BlogReasonContent)
                .HasMaxLength(300)
                .HasColumnName("blog_reason_content");
        });

        modelBuilder.Entity<Ticket>(entity =>
        {
            entity.HasKey(e => e.TicketId).HasName("PK__ticket__D596F96B071B6940");

            entity.ToTable("ticket");

            entity.Property(e => e.TicketId).HasColumnName("ticket_id");
            entity.Property(e => e.CreatorId).HasColumnName("creator_id");
            entity.Property(e => e.TicketContent)
                .HasMaxLength(300)
                .HasColumnName("ticket_content");
            entity.Property(e => e.TicketStatus).HasColumnName("ticket_status");
            entity.Property(e => e.TicketTime)
                .HasColumnType("datetime")
                .HasColumnName("ticket_time");
            entity.Property(e => e.TypeId).HasColumnName("type_id");

            entity.HasOne(d => d.Creator).WithMany(p => p.Tickets)
                .HasForeignKey(d => d.CreatorId)
                .HasConstraintName("FK__ticket__creator___05D8E0BE");

            entity.HasOne(d => d.Type).WithMany(p => p.Tickets)
                .HasForeignKey(d => d.TypeId)
                .HasConstraintName("FK__ticket__type_id__04E4BC85");
        });

        modelBuilder.Entity<TicketType>(entity =>
        {
            entity.HasKey(e => e.TypeId).HasName("PK__ticket_t__2C000598CB7803FC");

            entity.ToTable("ticket_type");

            entity.Property(e => e.TypeId).HasColumnName("type_id");
            entity.Property(e => e.TicketTypeContent)
                .HasMaxLength(200)
                .HasColumnName("ticket_type_content");
        });

        modelBuilder.Entity<User>(entity =>
        {
            entity.HasKey(e => e.UserId).HasName("PK__user__B9BE370F4FDD237D");

            entity.ToTable("user");

            entity.Property(e => e.UserId).HasColumnName("user_id");
            entity.Property(e => e.DisplayName)
                .HasMaxLength(55)
                .HasColumnName("display_name");
            entity.Property(e => e.FcmTokenId).HasColumnName("fcm_token_id");
            entity.Property(e => e.LastLogin)
                .HasColumnType("datetime")
                .HasColumnName("last_login");
            entity.Property(e => e.Password)
                .HasMaxLength(80)
                .HasColumnName("password");
            entity.Property(e => e.PasswordResetCode)
                .HasMaxLength(50)
                .IsUnicode(false);
            entity.Property(e => e.PasswordResetExpiration).HasColumnType("datetime");
            entity.Property(e => e.RoleId).HasColumnName("role_id");
            entity.Property(e => e.UserEmail)
                .HasMaxLength(100)
                .HasColumnName("user_email");
            entity.Property(e => e.UserImage).HasColumnName("user_image");
            entity.Property(e => e.UserPhone)
                .HasMaxLength(20)
                .IsUnicode(false)
                .HasColumnName("user_phone");
            entity.Property(e => e.UserStatus).HasColumnName("user_status");
            entity.Property(e => e.Username)
                .HasMaxLength(55)
                .HasColumnName("username");
            entity.Property(e => e.WalletBalance)
                .HasDefaultValue(0)
                .HasColumnName("wallet_balance");
        });

        modelBuilder.Entity<WalletTransaction>(entity =>
        {
            entity.HasKey(e => e.TransactionId).HasName("PK__wallet_t__85C600AFDFB5FA20");

            entity.ToTable("wallet_transaction");

            entity.Property(e => e.TransactionId).HasColumnName("transaction_id");
            entity.Property(e => e.Amount).HasColumnName("amount");
            entity.Property(e => e.OrderCode)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("order_code");
            entity.Property(e => e.TransactionDate)
                .HasDefaultValueSql("(getdate())")
                .HasColumnType("datetime")
                .HasColumnName("transaction_date");
            entity.Property(e => e.TransactionNote)
                .HasMaxLength(255)
                .HasColumnName("transaction_note");
            entity.Property(e => e.TransactionStatus)
                .HasMaxLength(20)
                .HasColumnName("transaction_status");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.User).WithMany(p => p.WalletTransactions)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__wallet_tr__user___2EA5EC27");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
