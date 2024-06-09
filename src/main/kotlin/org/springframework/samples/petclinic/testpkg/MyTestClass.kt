package org.springframework.samples.petclinic.testpkg

import io.opentelemetry.instrumentation.annotations.WithSpan

class MyTestClass {

    @WithSpan("my span on myTestMethod1")
    fun myTestMethod1(){
        println("in myTestMethod_1")
    }


    fun myTestMethod2(){
        println("in myTestMethod_2")
    }
}
