package com.hr.password.service

import com.hr.password.config.PasswordMailConfig
import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Date
import java.util.Properties

object PasswordMailService {

    suspend fun sendPasswordResetEmail(
        recipientEmail: String,
        recipientName: String,
        resetToken: String
    ) {
        withContext(Dispatchers.IO) {
            val encodedResetToken = URLEncoder.encode(
                resetToken,
                StandardCharsets.UTF_8
            )

            val resetUrl =
                "${PasswordMailConfig.frontendBaseUrl.trimEnd('/')}" +
                        "/reset-password" +
                        "?token=$encodedResetToken"

            val subject = "Reset your HR system password"

            val htmlContent = buildResetEmailHtml(
                recipientName = recipientName,
                resetUrl = resetUrl
            )

            sendHtmlEmail(
                recipientEmail = recipientEmail,
                subject = subject,
                htmlContent = htmlContent
            )
        }
    }

    suspend fun sendPasswordChangedEmail(
        recipientEmail: String,
        recipientName: String
    ) {
        withContext(Dispatchers.IO) {
            val subject = "Your HR system password was changed"

            val safeRecipientName = escapeHtml(recipientName)

            val htmlContent = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta
                        name="viewport"
                        content="width=device-width, initial-scale=1.0"
                    >
                    <title>Password Changed</title>
                </head>

                <body style="
                    margin: 0;
                    padding: 24px;
                    background: #f4f7fb;
                    font-family: Arial, sans-serif;
                    color: #1f2937;
                ">
                    <div style="
                        max-width: 620px;
                        margin: 0 auto;
                        background: #ffffff;
                        border-radius: 14px;
                        overflow: hidden;
                        box-shadow: 0 8px 30px rgba(15, 23, 42, 0.08);
                    ">
                        <div style="
                            padding: 24px;
                            color: #ffffff;
                            background: #4338ca;
                        ">
                            <h1 style="
                                margin: 0;
                                font-size: 24px;
                            ">
                                Password Changed
                            </h1>

                            <p style="
                                margin: 7px 0 0;
                                color: rgba(255, 255, 255, 0.82);
                            ">
                                Stool Lands HR System
                            </p>
                        </div>

                        <div style="padding: 28px;">
                            <p>
                                Hello $safeRecipientName,
                            </p>

                            <p style="
                                line-height: 1.7;
                                color: #475569;
                            ">
                                The password for your HR system account
                                was changed successfully.
                            </p>

                            <p style="
                                line-height: 1.7;
                                color: #475569;
                            ">
                                If you did not make this change, contact
                                the system administrator immediately.
                            </p>

                            <p style="
                                margin-top: 28px;
                                color: #64748b;
                                font-size: 13px;
                                line-height: 1.6;
                            ">
                                This is an automated security notification.
                            </p>
                        </div>
                    </div>
                </body>
                </html>
            """.trimIndent()

            sendHtmlEmail(
                recipientEmail = recipientEmail,
                subject = subject,
                htmlContent = htmlContent
            )
        }
    }

    private fun sendHtmlEmail(
        recipientEmail: String,
        subject: String,
        htmlContent: String
    ) {
        val properties = Properties().apply {
            put(
                "mail.smtp.host",
                PasswordMailConfig.host
            )

            put(
                "mail.smtp.port",
                PasswordMailConfig.port.toString()
            )

            put(
                "mail.smtp.auth",
                "true"
            )

            put(
                "mail.smtp.ssl.enable",
                PasswordMailConfig.useSsl.toString()
            )

            put(
                "mail.smtp.ssl.trust",
                PasswordMailConfig.host
            )

            put(
                "mail.smtp.connectiontimeout",
                "15000"
            )

            put(
                "mail.smtp.timeout",
                "15000"
            )

            put(
                "mail.smtp.writetimeout",
                "15000"
            )
        }

        val session = Session.getInstance(
            properties,
            object : Authenticator() {
                override fun getPasswordAuthentication():
                        PasswordAuthentication {

                    return PasswordAuthentication(
                        PasswordMailConfig.username,
                        PasswordMailConfig.password
                    )
                }
            }
        )

        val message = MimeMessage(session).apply {
            setFrom(
                InternetAddress(
                    PasswordMailConfig.defaultFromEmail,
                    "Stool Lands HR"
                )
            )

            setRecipient(
                Message.RecipientType.TO,
                InternetAddress(recipientEmail)
            )

            setSubject(
                subject,
                StandardCharsets.UTF_8.name()
            )

            setContent(
                htmlContent,
                "text/html; charset=UTF-8"
            )

            sentDate = Date()
        }

        println("Sending password email to: $recipientEmail")

        Transport.send(message)

        println("Password email sent successfully")
    }

    private fun buildResetEmailHtml(
        recipientName: String,
        resetUrl: String
    ): String {
        val safeRecipientName = escapeHtml(recipientName)
        val safeResetUrl = escapeHtml(resetUrl)

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta
                name="viewport"
                content="width=device-width, initial-scale=1.0"
            >
            <title>Password Reset</title>
        </head>

        <body style="
            margin: 0;
            padding: 24px;
            background: #f4f7fb;
            font-family: Arial, sans-serif;
            color: #1f2937;
        ">
            <div style="
                max-width: 620px;
                margin: 0 auto;
                background: #ffffff;
                border-radius: 14px;
                overflow: hidden;
                box-shadow: 0 8px 30px rgba(15, 23, 42, 0.08);
            ">
                <div style="
                    padding: 24px;
                    color: #ffffff;
                    background: #4338ca;
                ">
                    <h1 style="
                        margin: 0;
                        font-size: 24px;
                    ">
                        Password Reset
                    </h1>

                    <p style="
                        margin: 7px 0 0;
                        color: #e0e7ff;
                    ">
                        Stool Lands HR System
                    </p>
                </div>

                <div style="padding: 28px;">
                    <p>
                        Hello $safeRecipientName,
                    </p>

                    <p style="
                        line-height: 1.7;
                        color: #475569;
                    ">
                        A request was received to reset the password
                        for your HR system account.
                    </p>

                    <!-- Reset Password Button -->
                    <div style="
                        margin: 32px 0;
                        text-align: center;
                    ">
                        <a
                            href="$safeResetUrl"
                            target="_blank"
                            style="
                                display: inline-block;
                                padding: 15px 32px;
                                background-color: #4338ca;
                                color: #ffffff;
                                text-decoration: none;
                                font-family: Arial, sans-serif;
                                font-size: 16px;
                                font-weight: bold;
                                border-radius: 8px;
                                border: 1px solid #4338ca;
                            "
                        >
                            Reset Password
                        </a>
                    </div>

                    <p style="
                        color: #475569;
                        line-height: 1.7;
                    ">
                        This link expires in 30 minutes and can be
                        used only once.
                    </p>

                    <p style="
                        color: #475569;
                        line-height: 1.7;
                    ">
                        If you did not request a password reset,
                        ignore this message. Your password will
                        remain unchanged.
                    </p>

                    <p style="
                        margin-top: 28px;
                        color: #94a3b8;
                        font-size: 12px;
                        line-height: 1.6;
                        overflow-wrap: anywhere;
                        word-break: break-word;
                    ">
                        If the button does not work, copy and paste
                        this URL into your browser:
                        <br><br>
                        <a
                            href="$safeResetUrl"
                            style="
                                color: #4338ca;
                                text-decoration: underline;
                                overflow-wrap: anywhere;
                                word-break: break-all;
                            "
                        >$safeResetUrl</a>
                    </p>
                </div>
            </div>
        </body>
        </html>
    """.trimIndent()
    }


    private fun escapeHtml(
        value: String
    ): String {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}