# Email Configuration Guide

## Setting Up Email for OTP Verification

To enable OTP email verification, you need to configure your email settings in `application.properties`.

### For Gmail:

1. **Enable 2-Step Verification** on your Gmail account
2. **Generate an App Password**:
   - Go to your Google Account settings
   - Navigate to Security → 2-Step Verification → App passwords
   - Generate a new app password for "Mail"
   - Copy the 16-character password

3. **Update `application.properties`**:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-16-character-app-password
   ```

### For Other Email Providers:

#### Outlook/Hotmail:
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

#### Yahoo:
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=your-email@yahoo.com
spring.mail.password=your-app-password
```

### Testing Email Configuration

After configuring, restart the Spring Boot application and try registering a new user. The OTP will be sent to the email address provided during registration.

### Important Notes:

- **Gmail requires App Passwords** - Regular passwords won't work with 2-Step Verification enabled
- **OTP expires in 5 minutes** - Users must verify within this time
- **Check spam folder** - Sometimes emails may be filtered

### Troubleshooting:

If emails are not being sent:
1. Verify email credentials are correct
2. Check firewall/network settings
3. Ensure SMTP port (587) is not blocked
4. For Gmail, make sure "Less secure app access" is enabled OR use App Passwords
5. Check application logs for error messages
