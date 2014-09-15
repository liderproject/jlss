package jlss


object JLSSUtils {
  private val urlCharset = "charset=(.*)".r

  def readURL(url : java.net.URL) : java.io.Reader = {
    val conn = url.openConnection()

    val charSet = conn.getContentType().split(";").find(_.trim().toLowerCase().startsWith("charset=")) match {
      case Some(urlCharset(cs)) => cs
      case None => "UTF-8" // Assumption
    }

    new java.io.InputStreamReader(conn.getInputStream(), charSet)
  }
}
