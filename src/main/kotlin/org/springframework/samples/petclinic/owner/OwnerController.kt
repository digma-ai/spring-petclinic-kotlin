/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner

import io.opentelemetry.context.Context
import io.opentelemetry.extension.kotlin.asContextElement
import jakarta.validation.Valid
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import org.springframework.samples.petclinic.processors.*
import org.springframework.samples.petclinic.testpkg.MyTestClass
import org.springframework.samples.petclinic.visit.VisitRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Antoine Rey
 */
@Controller
class OwnerController(val owners: OwnerRepository, val visits: VisitRepository) {

    val VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm"

    companion object Logger{
        private const val MY_ID = "myId"
//        fun log(msg: String){
//            logMsg("$MY_ID $msg")
//        }
    }


    @InitBinder
    fun setAllowedFields(dataBinder: WebDataBinder) {
//        log("setAllowedFields")
        dataBinder.setDisallowedFields("id")
    }

    @GetMapping("/owners/new")
    fun initCreationForm(model: MutableMap<String, Any>): String {
        val owner = Owner()
        model["owner"] = owner
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM
    }

    @PostMapping("/owners/new")
    fun processCreationForm(@Valid owner: Owner, result: BindingResult): String {
        return if (result.hasErrors()) {
            VIEWS_OWNER_CREATE_OR_UPDATE_FORM
        } else {
            owners.save(owner)
            "redirect:/owners/" + owner.id
        }
    }

    @GetMapping("/owners/find")
    fun initFindForm(model: MutableMap<String, Any>): String {
        model["owner"] = Owner()
        return "owners/findOwners"
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class, DelicateCoroutinesApi::class)
    @GetMapping("/owners")
    fun processFindForm(owner: Owner, result: BindingResult, model: MutableMap<String, Any>): String {
        // find owners by last name

        //print all injected annotations, also see that classes are transformed only here when first loaded
        //the exclude list is:
        //ReflectionUtils;MyClassShouldNotBeTransformed;MyClassPartiallyTransformed.doNotAnnotate;*excludedWithPattern
        ReflectionUtils.printClassAnnotations(ReflectionUtils) //should not be transformed
        ReflectionUtils.printClassAnnotations(MyClassShouldNotBeTransformed::class.java) //should not be transformed
        ReflectionUtils.printClassAnnotations(MyClassPartiallyTransformed::class.java) //partially transformed, only some methods
        ReflectionUtils.printClassAnnotations(SearchHistoryProcessor::class.java) //should be transformed and not fail on duplicate annotation because already has @WithSpan
        ReflectionUtils.printClassAnnotations(ViewedOwnersProcessor::class.java)  //should be transformed
        ReflectionUtils.printClassAnnotations(MyTestClass::class.java)

        val myTestClass = MyTestClass()
        myTestClass.myTestMethod1()
        myTestClass.myTestMethod2()



        GlobalScope.launch(Context.current().asContextElement()) {
            SearchHistoryProcessor().saveToHistory(owner)
        }

        val results = owners.findByLastName(owner.lastName)
        return when {
            results.isEmpty() -> {
                // no owners found
                result.rejectValue("lastName", "notFound", "not found")
                "owners/findOwners"
            }
            results.size == 1 -> {
                // 1 owner found
                "redirect:/owners/" + results.first().id
            }
            else -> {

                GlobalScope.launch(Context.current().asContextElement()) {
                    ViewedOwnersProcessor().processOwners(flow {
                        results.forEach {
                            delay(1000)
                            println("in results.forEach owner:${it.firstName} ${it.lastName},  t=${Thread.currentThread().name}")
                            emit(it)
                        }
                    },"my process owners")
                }

                // multiple owners found
                model["selections"] = results
                "owners/ownersList"
            }
        }
    }

    @GetMapping("/owners/{ownerId}/edit")
    fun initUpdateOwnerForm(@PathVariable("ownerId") ownerId: Int, model: Model): String {
        val owner = owners.findById(ownerId)
        model.addAttribute(owner)
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM
    }

    @PostMapping("/owners/{ownerId}/edit")
    fun processUpdateOwnerForm(@Valid owner: Owner, result: BindingResult, @PathVariable("ownerId") ownerId: Int): String {
        return if (result.hasErrors()) {
            VIEWS_OWNER_CREATE_OR_UPDATE_FORM
        } else {
            owner.id = ownerId
            this.owners.save(owner)
            "redirect:/owners/{ownerId}"
        }
    }

    /**
     * Custom handler for displaying an owner.
     *
     * @param ownerId the ID of the owner to display
     * @return the view
     */
    @GetMapping("/owners/{ownerId}")
    fun showOwner(@PathVariable("ownerId") ownerId: Int, model: Model): String {
        val owner = this.owners.findById(ownerId)
        for (pet in owner.getPets()) {
            pet.visits = visits.findByPetId(pet.id!!)
        }
        model.addAttribute(owner)
        return "owners/ownerDetails"
    }

}

