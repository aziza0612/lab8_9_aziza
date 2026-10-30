package com.example.lab8_9.lab8

class PermissionModel {
    var granted: Boolean = false
        private set

    var rationale: Boolean = false
        private set

    var dialogShows: Boolean = true
        private set

    private var denyCount: Int = 0

    fun request(userAllows: Boolean) {
        if (!dialogShows) return

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
