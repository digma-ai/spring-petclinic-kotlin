package org.springframework.samples.petclinic.processors

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import org.springframework.samples.petclinic.owner.Owner

class ViewedOwnersProcessor {


    //to instrument with otel methods instrumentation
    //comment all @WithSpan and add environment variable
    //OTEL_INSTRUMENTATION_METHODS_INCLUDE=
    // org.springframework.samples.petclinic.processors.ViewedOwnersProcessor[processOwners,getOwnerName,filterByName,getExcludeList]

    //    @ExperimentalCoroutinesApi
//    @FlowPreview
    suspend fun processOwners(owners: Flow<Owner>, name: String) {

        println("in processOwners, t=" + Thread.currentThread().name)

        doSomething(name)

        owners
            .mapNotNull(name = "MyMapNotNullName") {
                delay(100)
                println("in mapNotNull lambda,owner:${it.firstName} ${it.lastName}, t=" + Thread.currentThread().name)
                getOwnerName(it)
            }
            .filter(name = "MyFilterName") {
                delay(100)
                println("in filter lambda, name:$it, t=" + Thread.currentThread().name)
                filterByName(it)
            }
            .collect {
                delay(100)
                println("in collect lambda, name:$it, t=" + Thread.currentThread().name)
                println("found $it at ${kotlinx.datetime.Clock.System.now()}")
            }
    }

    private fun doSomething(name: String) {
        println("in doSomething, t=" + Thread.currentThread().name)
    }


    suspend fun getOwnerName(owner: Owner): String {
        delay(100)
        return owner.firstName
    }

    suspend fun filterByName(ownerName: String): Boolean {
        delay(100)
        println("in filterByName, name:$ownerName, t=" + Thread.currentThread().name)
        return !getExcludeList().contains(ownerName)
    }

    suspend fun getExcludeList(): List<String> {
        delay(10)
        println("in getExcludeList, t=" + Thread.currentThread().name)
        return listOf("Carlos")
    }

}
