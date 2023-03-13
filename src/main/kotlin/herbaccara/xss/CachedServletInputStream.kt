package herbaccara.xss

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import java.io.ByteArrayInputStream

class CachedServletInputStream(
    private val cached: ByteArrayInputStream
) : ServletInputStream() {

    override fun read(): Int {
        return cached.read()
    }

    override fun isFinished(): Boolean {
        return try {
            cached.available() == 0
        } catch (e: Exception) {
            false
        }
    }

    override fun isReady(): Boolean = true

    override fun setReadListener(readListener: ReadListener) {
        // ignore
    }
}
