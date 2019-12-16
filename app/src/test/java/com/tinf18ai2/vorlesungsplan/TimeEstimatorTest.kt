package com.tinf18ai2.vorlesungsplan

import com.tinf18ai2.vorlesungsplan.models.FABDataModel
import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import com.tinf18ai2.vorlesungsplan.services.ServiceFactory
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat

class TimeEstimatorTest {


    @Test
    fun test_estimation() {
        val item1 = VorlesungsplanItem("Test1","09:00","Raum 0", SimpleDateFormat("HH:mm").parse("09:00")!!, SimpleDateFormat("HH:mm").parse("12:00")!!,0)
        val item2 = VorlesungsplanItem("Test2","13:00","Raum 1", SimpleDateFormat("HH:mm").parse("13:00")!!, SimpleDateFormat("HH:mm").parse("16:00")!!,0)
        val item3 = VorlesungsplanItem("Test1","09:00","Raum 0", SimpleDateFormat("HH:mm").parse("09:00")!!, SimpleDateFormat("HH:mm").parse("12:00")!!,0)
        val item4 = VorlesungsplanItem("Test2","13:00","Raum 1", SimpleDateFormat("HH:mm").parse("13:00")!!, SimpleDateFormat("HH:mm").parse("16:00")!!,0)
        val items = ArrayList<VorlesungsplanItem>()
        items.add(item1)
        items.add(item2)
        val tag1 = Vorlesungstag("Donnerstag, 05.12.",items,SimpleDateFormat("dd.MM").parse("05.12")!!)
        items.remove(item1)
        items.remove(item2)
        items.add(item3)
        items.add(item4)
        val tag2 = Vorlesungstag("Freitag, 06.12.",items,SimpleDateFormat("dd.MM").parse("06.12")!!)
        val week = ArrayList<Vorlesungstag>()
        week.add(tag1)
        week.add(tag2)

        TODO("INIT MOCK SERVICES")
        val erg : FABDataModel? = ServiceFactory.getTimeEstimation().estimate().blockingGet()
        Assert.assertNotNull(erg)
        Assert.assertEquals(erg!!.days , 0)
    }
}