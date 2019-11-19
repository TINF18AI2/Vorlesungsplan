package com.tinf18ai2.vorlesungsplan

import com.tinf18ai2.vorlesungsplan.backend_services.StateData
import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ObservableTest {
    @Test
    fun test_basic() {
        StateData.subscribeOn(Schedulers.io()).observeOn(Schedulers.single())
            .subscribe { list -> print(list) }

        val item1 = VorlesungsplanItem("title", "time", "description", Date(), Date())
        var list: ArrayList<VorlesungsplanItem> = ArrayList()
        list.add(item1)
        StateData.updateData(list)
    }
}
