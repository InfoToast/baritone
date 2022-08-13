/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
* Info Toast tries our best to attribute all of the authors of baritone source code properly.
* However, sometimes weird things can occur, and people may not necessarily be able to show up on the git log.
* We normally cherry-pick commits, but sometimes that doesn't work.
* Such is the case with this file, and the bug fix that created the majority of it.
* There were two commits that altered this file. One of which was a commit made several days prior by wagyourtail.
* Wagyourtail's commit was made to the 1.19 branch. However, someone who was on 1.19.2, unknowing of this commit
* fixed it independently and submitted a pull request to the 1.19.2 branch. That person's GitHub account name was KosmX.
* After careful consideration, and asking the opinion of a second party, we decided to go with KosmX's commit.
* KosmX's commit simply had cleaner code. There's not much else to it.
* Unfortunately, KosmX deleted his git repository when wagyourtail closed the pull request, meaning we were unable
* to truly cherry-pick this file, and thus the author of the commit will be listed as FrankTCA.
* However, all credit (except for this long and drawn out comment that likely  will never be read by anyone)
* should go to:
* KosmX
*
* - Frank from Info Toast
 */

package baritone.launch.mixins;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.ChatEvent;
import baritone.utils.accessor.IGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.net.URI;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class MixinScreen implements IGuiScreen {

    @Override
    @Invoker("openLink")
    public abstract void openLinkInvoker(URI url);

    @Inject(method = "handleComponentClicked", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/chat/ClickEvent;getAction()Lnet/minecraft/network/chat/ClickEvent$Action;"), cancellable = true)
    private void fixBaritoneClickCommand(Style arg, CallbackInfoReturnable<Boolean> ci) {
        ClickEvent clickEvent = arg.getClickEvent();
        if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            IBaritone baritone = BaritoneAPI.getProvider().getBaritoneForPlayer(Minecraft.getInstance().player);
            if (baritone == null) {
                return;
            }

            ChatEvent event = new ChatEvent(clickEvent.getValue());
            baritone.getGameEventHandler().onSendChatMessage(event);
            if (event.isCancelled()) {
                ci.setReturnValue(true);
            }
        }
    }
}