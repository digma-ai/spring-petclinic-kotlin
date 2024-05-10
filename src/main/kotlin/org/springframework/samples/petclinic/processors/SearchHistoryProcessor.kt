package org.springframework.samples.petclinic.processors

import io.opentelemetry.instrumentation.annotations.WithSpan
import jakarta.annotation.Nullable
import kotlinx.coroutines.delay
import org.springframework.samples.petclinic.owner.Owner

class SearchHistoryProcessor {


    //to instrument with otel methods instrumentation
    //comment all @WithSpan and add environment variable
    //OTEL_INSTRUMENTATION_METHODS_INCLUDE=
    // org.springframework.samples.petclinic.processors.SearchHistoryProcessor[saveToHistory,saveOwnerToHistory]

    @Nullable // just check that it still exists after transformation
    //bytebuddy transformation should detect that this method already has WithSpan and not transform this method, if we transform this method and add two WithSpan
    // annotations the code in ReflectionUtils.printClassAnnotations(this) will crash when calling method.annotations because the annotations
    // are not valid bytecode
    @WithSpan(value = "my span name")
    suspend fun saveToHistory(owner: Owner):Boolean {
        return saveOwnerToHistory(owner)
    }

    @Nullable // just check that it still exists after transformation
//    @WithSpan
    suspend fun saveOwnerToHistory( @Nullable owner: Owner):Boolean  {
        delay(100)
        println("saving ${owner.firstName}  to history")
        return true
    }

}
