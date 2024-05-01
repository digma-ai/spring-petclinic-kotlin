package org.springframework.samples.petclinic.processors

import io.opentelemetry.instrumentation.annotations.WithSpan
import kotlinx.coroutines.delay
import org.springframework.samples.petclinic.owner.Owner

class SearchHistoryProcessor {

    //to instrument with otel methods instrumentation
    //comment all @WithSpan and add environment variable
    //OTEL_INSTRUMENTATION_METHODS_INCLUDE=
    // org.springframework.samples.petclinic.processors.SearchHistoryProcessor[saveToHistory,saveOwnerToHistory]

    //@WithSpan
    suspend fun saveToHistory(owner: Owner) {
        saveOwnerToHistory(owner)
    }

    //@WithSpan
    private suspend fun saveOwnerToHistory(owner: Owner) {
        delay(100)
        println("saving ${owner.firstName}  to history")

    }

}
