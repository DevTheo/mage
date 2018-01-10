/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.b;

import java.util.UUID;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author LevelX2
 */
public class BlazingHope extends CardImpl {

    public BlazingHope(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{W}");

        // Exile target creature with power greater than or equal to your life total.
        this.getSpellAbility().addEffect(new ExileTargetEffect());
        this.getSpellAbility().addTarget(new BlazingHopeTarget());
    }

    public BlazingHope(final BlazingHope card) {
        super(card);
    }

    @Override
    public BlazingHope copy() {
        return new BlazingHope(this);
    }
}

class BlazingHopeTarget extends TargetCreaturePermanent {

    public BlazingHopeTarget() {
        super(new FilterCreaturePermanent("creature with power greater than or equal to your life total"));
    }

    public BlazingHopeTarget(final BlazingHopeTarget target) {
        super(target);
    }

    @Override
    public boolean canTarget(UUID controllerId, UUID id, Ability source, Game game) {
        Permanent permanent = game.getPermanent(id);
        if (permanent != null) {
            if (!isNotTarget()) {
                if (!permanent.canBeTargetedBy(game.getObject(source.getId()), controllerId, game)
                        || !permanent.canBeTargetedBy(game.getObject(source.getSourceId()), controllerId, game)) {
                    return false;
                }
            }
            Player controller = game.getPlayer(source.getControllerId());
            if (controller != null && permanent.getPower().getValue() >= controller.getLife()) {
                return filter.match(permanent, source.getSourceId(), controllerId, game);
            }
        }
        return false;
    }

    @Override
    public boolean canChoose(UUID sourceId, UUID sourceControllerId, Game game) {
        int remainingTargets = this.minNumberOfTargets - targets.size();
        if (remainingTargets <= 0) {
            return true;
        }
        int count = 0;
        Player controller = game.getPlayer(sourceControllerId);
        MageObject targetSource = game.getObject(sourceId);
        for (Permanent permanent : game.getBattlefield().getActivePermanents(filter, sourceControllerId, sourceId, game)) {
            if (!targets.containsKey(permanent.getId())) {
                if (notTarget || permanent.canBeTargetedBy(targetSource, sourceControllerId, game)) {
                    if (controller != null && permanent.getPower().getValue() >= controller.getLife()) {
                        count++;
                        if (count >= remainingTargets) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public BlazingHopeTarget copy() {
        return new BlazingHopeTarget(this);
    }
}