package com.splife

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.bind.annotation.*

@SpringBootApplication
open class DemoApplication{
    @Bean
    open fun objectMapperBuilder(): Jackson2ObjectMapperBuilder
            = Jackson2ObjectMapperBuilder().modulesToInstall(KotlinModule())
}

// Model
data class Event(var name:String) {
    val id:Int = getIncrementalId()

    companion object {
        private var id = 1
        fun getIncrementalId() : Int {
            return id++
        }
    }
}

// Collection
val eventList = mutableListOf(Event("Tour de Pain"), Event("Gate River Run"))


@RestController
@RequestMapping("api/v3/events")
class EventController  {
    @RequestMapping
    fun findAll() = eventList

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    fun createOne(@RequestBody event:Event) = eventList.add(event)

    @RequestMapping("{id}")
    fun findOne(@PathVariable("id") id: Int) = eventList.find { it.id.equals(id) }

    @RequestMapping("{id}", method = arrayOf(RequestMethod.PUT, RequestMethod.PATCH))
    fun updateOne(@RequestBody updatedEvent: Event, @PathVariable("id") id: Int){
        var eventToUpdate = findOne(id)
        eventToUpdate?.name = updatedEvent.name
    }

    @RequestMapping("{id}", method = arrayOf(RequestMethod.DELETE))
    fun destroyOne(@PathVariable("id") id: Int) =  eventList.removeAll { it.id.equals(id) }
}

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}
