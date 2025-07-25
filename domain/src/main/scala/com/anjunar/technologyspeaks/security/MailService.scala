package com.anjunar.technologyspeaks.security

import com.anjunar.technologyspeaks.control.{EMail, User}
import com.google.common.io.Resources
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.mail.internet.{InternetAddress, MimeMessage}
import jakarta.mail.{Address, Message, Session}
import jakarta.transaction.Transactional
import jakarta.transaction.Transactional.TxType
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

import java.nio.charset.Charset
import java.util
import java.util.Locale
import scala.compiletime.uninitialized

@ApplicationScoped
class MailService {

  @Inject var session: Session = uninitialized

  @Transactional(TxType.REQUIRES_NEW)
  def send(email: String, variables : util.HashMap[String, AnyRef], templateRef: String, subject : String): Unit = {
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress("anjunar@gmx.de"))
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email).asInstanceOf[Array[Address]])
    message.setSubject(subject)

    val url = Resources.getResource(templateRef)
    val html = Resources.toString(url, Charset.forName("UTF-8"))

    val context = new Context(Locale.GERMAN)
    context.setVariables(variables)

    val engine = new TemplateEngine
    val result = engine.process(html, context)

    message.setContent(result, "text/html; charset=UTF-8");
    val transport = session.getTransport
    transport.connect()
    transport.sendMessage(message, InternetAddress.parse(email).asInstanceOf[Array[Address]])
  }


}
