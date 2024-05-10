package org.springframework.samples.petclinic.processors

object ReflectionUtils {


    fun printClassAnnotations(obj: Any) {
        printClassAnnotations(obj::class.java)
    }

    fun printClassAnnotations(clazz: Class<*>) {
        try {

//            val methods = clazz.methods
            val declaredMethods = clazz.declaredMethods


            for (method in declaredMethods) {

                if (method.declaringClass == Any::class.java){
                    continue
                }

                val annotations = method.annotations
                val annotationsNames = annotations.contentToString()

//                println("Method " + obj.javaClass.name + "." + method.name + " has annotations " + annotationsNames)
                println("Method $method has annotations $annotationsNames")
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
