package com.tinf18ai2.vorlesungsplan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: VorlesungsplanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linearLayoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = linearLayoutManager
        setSupportActionBar(toolbar)
        print("test")

        adapter = VorlesungsplanAdapter(items = getData(), context = applicationContext)
        mainRecyclerView.adapter = adapter
        print("MAINRV" + mainRecyclerView)

    }

    private fun getData(): List<VorlesungsplanItem> {
        return listOf(
            VorlesungsplanItem(
                "test",
                time = "Zeit",
                description = "Test"
            ), VorlesungsplanItem(
                "test",
                time = "Zeit",
                description = "Test"
            ), VorlesungsplanItem(
                "test",
                time = "Zeit",
                description = "Test"
            ), VorlesungsplanItem(
                "test",
                time = "Zeit",
                description = "Test"
            )
        )
    }

    private fun displayPlan() {
        var site: Document =
            readWebsite("https://vorlesungsplan.dhbw-mannheim.de/index.php?action=view&gid=3067001&uid=7431001")

        var days = site.getElementsByClass("ui-grid-e").first().allElements

        for (day in days) {

        }
    }

    private fun readWebsite(url: String): Document {
        return Jsoup.connect(url).get()
    }
}