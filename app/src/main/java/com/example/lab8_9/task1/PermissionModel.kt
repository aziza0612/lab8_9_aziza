package com.example.lab8_9.task1

/**
 * 1-topshiriq: PermissionModel klassi - Android runtime permission "Denied twice" modelini simulyatsiya qiladi.
 *
 * Xossalari:
 * @property granted ruxsat berilganligi
 * @property rationale tushuntirish berish kerakligi (faqat bir marta rad etilganda true bo'ladi)
 * @property dialogShows tizim dialogi ko'rinishi
 */
class PermissionModel {
    var granted: Boolean = false
        private set

    var rationale: Boolean = false
        private set

    var dialogShows: Boolean = true
        private set

    private var denyCount: Int = 0

    fun request(userAllows: Boolean) {
        if (!dialogShows) {
            println("dialog yo'q, darhol rad")
            return
        }

        if (userAllows) {
            granted = true
            rationale = false
            dialogShows = false
        } else {
            denyCount++
            granted = false
            if (denyCount == 1) {
                rationale = true
                dialogShows = true
            } else {
                // denyCount >= 2
                rationale = false
                dialogShows = false
            }
        }
    }

    fun reset() {
        granted = false
        rationale = false
        dialogShows = true
        denyCount = 0
    }

    override fun toString(): String {
        return "granted=$granted, rationale=$rationale, dialogShows=$dialogShows"
    }
}

fun main() {
    val model = PermissionModel()
    println("Boshlang'ich holat: $model")

    println("\n1-qadam (rad):")
    model.request(userAllows = false)
    println(model)

    println("\n2-qadam (rad):")
    model.request(userAllows = false)
    println(model)

    println("\n3-qadam (so'rov):")
    model.request(userAllows = false)
    println(model)

    println("\n4-qadam (ruxsat):")
    model.request(userAllows = true)
    println(model)
}
