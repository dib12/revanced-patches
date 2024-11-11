package app.revanced.patches.youtube.layout.hide.watermark
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.youtube.misc.litho.filter.addLithoFilter
import app.revanced.patches.youtube.misc.litho.filter.lithoFilterPatch

private const val LAYOUT_COMPONENTS_FILTER_CLASS_DESCRIPTOR =
    "Lapp/revanced/extension/youtube/patches/components/LayoutComponentsFilter;"

@Suppress("unused")
val HideWatermarkPatch = bytecodePatch(
    name = "Hide watermark",
    description = "Adds options to hide watermark.",

) {
    dependsOn(lithoFilterPatch)

    compatibleWith(
        "com.google.android.youtube"(
            "18.38.44",
            "18.49.37",
            "19.16.39",
            "19.25.37",
            "19.34.42",
            "19.43.41",
        ),
    )

    execute {
        addLithoFilter(LAYOUT_COMPONENTS_FILTER_CLASS_DESCRIPTOR)

        showWatermarkFingerprint.match(
            playerOverlayFingerprint.originalClassDef,
        ).method.apply {
            val index = implementation!!.instructions.size - 5

            removeInstruction(index)
            addInstructions(
                index,
                """
                    invoke-static {}, $LAYOUT_COMPONENTS_FILTER_CLASS_DESCRIPTOR->showWatermark()Z
                    move-result p2
                """,
            )
        }
    }
}
