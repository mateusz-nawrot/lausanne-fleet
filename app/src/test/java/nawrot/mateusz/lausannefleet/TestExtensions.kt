package nawrot.mateusz.lausannefleet

import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals


inline fun <reified T : Any> Observer<T>.verifyValues(vararg values: T) {
    argumentCaptor<T>().apply {
        verify(this@verifyValues, times(values.size)).onChanged(capture())
        values.forEach {
            assertEquals(it, allValues[values.indexOf(it)])
        }
        reset(this@verifyValues)
    }
}