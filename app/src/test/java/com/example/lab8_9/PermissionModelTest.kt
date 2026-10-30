package com.example.lab8_9

import com.example.lab8_9.task1.PermissionModel
import org.junit.Assert.*
import org.junit.Test

class PermissionModelTest {

    @Test
    fun testPermissionModelFourSteps() {
        val model = PermissionModel()

        // Initial state
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertTrue(model.dialogShows)

        // 1-qadam: Rad etish #1
        model.request(userAllows = false)
        assertFalse(model.granted)
        assertTrue(model.rationale) // Rationale is true ONLY after 1st denial
        assertTrue(model.dialogShows)

        // 2-qadam: Rad etish #2
        model.request(userAllows = false)
        assertFalse(model.granted)
        assertFalse(model.rationale) // Rationale becomes false after 2nd denial
        assertFalse(model.dialogShows) // Dialog won't show anymore

        // 3-qadam: So'rov (When dialogShows is false, dialog is not shown)
        model.request(userAllows = false)
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertFalse(model.dialogShows)

        // 4-qadam: Ruxsat so'rovi (Still denied because dialog cannot be shown)
        model.request(userAllows = true)
        assertFalse(model.granted)
        assertFalse(model.dialogShows)
    }

    @Test
    fun testPermissionGrantedOnFirstTry() {
        val model = PermissionModel()
        model.request(userAllows = true)
        assertTrue(model.granted)
        assertFalse(model.rationale)
        assertFalse(model.dialogShows)
    }
}
