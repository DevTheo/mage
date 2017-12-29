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
package mage.cards.f;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.StateTriggeredAbility;
import mage.abilities.common.BeginningOfEndStepTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.effects.common.RemoveAllCountersSourceEffect;
import mage.abilities.effects.common.SacrificeSourceEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.events.DamageEvent;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.permanent.Permanent;

/**
 *
 * @author emerald000 & L_J
 */
public class ForceBubble extends CardImpl {

    public ForceBubble(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.ENCHANTMENT},"{2}{W}{W}");

        // If damage would be dealt to you, put that many depletion counters on Force Bubble instead.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new ForceBubbleReplacementEffect()));

        // When there are four or more depletion counters on Force Bubble, sacrifice it.
        this.addAbility(new ForceBubbleStateTriggeredAbility());

        // At the beginning of each end step, remove all depletion counters from Force Bubble.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(new RemoveAllCountersSourceEffect(CounterType.DEPLETION), TargetController.ANY, false));
    }

    public ForceBubble(final ForceBubble card) {
        super(card);
    }

    @Override
    public ForceBubble copy() {
        return new ForceBubble(this);
    }
}

class ForceBubbleReplacementEffect extends ReplacementEffectImpl {

    ForceBubbleReplacementEffect() {
        super(Duration.WhileOnBattlefield, Outcome.PreventDamage);
        staticText = "If damage would be dealt to you, put that many depletion counters on {this} instead";
    }

    ForceBubbleReplacementEffect(final ForceBubbleReplacementEffect effect) {
        super(effect);
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        DamageEvent damageEvent = (DamageEvent) event;
        new AddCountersSourceEffect(CounterType.DEPLETION.createInstance(damageEvent.getAmount()), true).apply(game, source);
        return true;
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == EventType.DAMAGE_PLAYER;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        return event.getTargetId().equals(source.getControllerId());
    }

    @Override
    public ForceBubbleReplacementEffect copy() {
        return new ForceBubbleReplacementEffect(this);
    }
}

class ForceBubbleStateTriggeredAbility extends StateTriggeredAbility {

    public ForceBubbleStateTriggeredAbility() {
        super(Zone.BATTLEFIELD, new SacrificeSourceEffect());
    }

    public ForceBubbleStateTriggeredAbility(final ForceBubbleStateTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public ForceBubbleStateTriggeredAbility copy() {
        return new ForceBubbleStateTriggeredAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        Permanent permanent = game.getPermanent(getSourceId());
        return permanent != null && permanent.getCounters(game).getCount(CounterType.DEPLETION) >= 4;
    }

    @Override
    public String getRule() {
        return "When there are four or more depletion counters on {this}, sacrifice it.";
    }
}