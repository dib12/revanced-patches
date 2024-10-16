package app.revanced.patches.youtube.player.watermark

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patches.shared.litho.LithoFilterPatch
import app.revanced.patches.youtube.player.components.fingerprints.WatermarkFingerprint
import app.revanced.patches.youtube.player.components.fingerprints.WatermarkParentFingerprint
import app.revanced.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.integrations.Constants.COMPONENTS_PATH
import app.revanced.patches.youtube.utils.integrations.Constants.PLAYER_CLASS_DESCRIPTOR
import app.revanced.util.patch.BaseBytecodePatch
import app.revanced.util.resultOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

@Suppress("unused")
object ChannelWatermarkPatch : BaseBytecodePatch(
    name = "Hide channel watermark",
    description = "Adds an option to hide creator's watermarks in the video player.",
    dependencies = setOf(
        LithoFilterPatch::class,
    ),
    compatiblePackages = COMPATIBLE_PACKAGE,
    fingerprints = setOf(WatermarkParentFingerprint)
) {
    private const val PLAYER_COMPONENTS_FILTER_CLASS_DESCRIPTOR =
        "$COMPONENTS_PATH/WaterMarkFilter;"

    override fun execute(context: BytecodeContext) {

        // region patch for hide channel watermark

        WatermarkFingerprint.resolve(
            context,
            WatermarkParentFingerprint.resultOrThrow().classDef
        )
        WatermarkFingerprint.resultOrThrow().let {
            it.mutableMethod.apply {
                val insertIndex = it.scanResult.patternScanResult!!.endIndex
                val register = getInstruction<TwoRegisterInstruction>(insertIndex).registerA

                addInstructions(
                    insertIndex + 1, """
                        invoke-static {v$register}, $PLAYER_CLASS_DESCRIPTOR->hideChannelWatermark(Z)Z
                        move-result v$register
                        """
                )
            }
        }

        // endregion

        LithoFilterPatch.addFilter(PLAYER_COMPONENTS_FILTER_CLASS_DESCRIPTOR)
    }

}
