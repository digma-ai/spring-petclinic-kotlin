package org.springframework.samples.petclinic.processors

import io.opentelemetry.instrumentation.annotations.WithSpan
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import org.springframework.samples.petclinic.owner.Owner

class ViewedOwnersProcessor {


    //to instrument with otel methods instrumentation
    //comment all @WithSpan and add environment variable
    //OTEL_INSTRUMENTATION_METHODS_INCLUDE=
    // org.springframework.samples.petclinic.processors.ViewedOwnersProcessor[processOwners,getOwnerName,filterByName,getExcludeList]

    @ExperimentalCoroutinesApi
    @FlowPreview
//    @WithSpan
    suspend fun processOwners(owners: Flow<Owner>){

        owners
            .mapNotNull {
                getOwnerName(it)
            }
            .filter { filterByName(it) }
            .collect{
                println("found $it at ${kotlinx.datetime.Clock.System.now()}")
            }
    }


//    @WithSpan
    suspend fun getOwnerName(owner: Owner): String {
        delay(100)
        return owner.firstName
    }

//    @WithSpan
    suspend fun filterByName(ownerName: String): Boolean {
        delay(100)
        return !getExcludeList().contains(ownerName)
    }

//    @WithSpan
    suspend fun getExcludeList():List<String>{
        delay(10)
        return listOf("Carlos")
    }

}
