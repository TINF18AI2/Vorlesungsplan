package com.tinf18ai2.vorlesungsplan.backend_services

import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import java.text.SimpleDateFormat

class ListItemProvider {
    companion object {
        fun getAllListItems(week: List<Vorlesungstag>): ArrayList<VorlesungsplanItem> {
            val allItems: ArrayList<VorlesungsplanItem> = ArrayList()

            for (day in week) {
                allItems.add(
                    VorlesungsplanItem(
                        day.tag,
                        "",
                        "",
                        SimpleDateFormat("dd.MM").parse("00.00"),
                        SimpleDateFormat("dd.MM").parse("00.00")
                    )
                )
                for (item in day.items) {
                    allItems.add(item)
                }
            }
            return allItems
        }
    }
}