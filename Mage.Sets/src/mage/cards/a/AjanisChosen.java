
package mage.cards.a;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldAllTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.common.FilterControlledEnchantmentPermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.CatToken;
import mage.game.permanent.token.TokenImpl;
import mage.game.permanent.token.Token;
import mage.players.Player;

import java.util.UUID;

/**
 *
 * @author Plopman
 */
public final class AjanisChosen extends CardImpl {

    public AjanisChosen(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{W}{W}");
        this.subtype.add(SubType.CAT);
        this.subtype.add(SubType.SOLDIER);

        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // Whenever an enchantment enters the battlefield under your control, create a 2/2 white Cat creature token. If that enchantment is an Aura, you may attach it to the token.
        this.addAbility(new EntersBattlefieldAllTriggeredAbility(
                Zone.BATTLEFIELD, new AjanisChosenEffect(), new FilterControlledEnchantmentPermanent(), false, SetTargetPointer.PERMANENT,
                "Whenever an enchantment enters the battlefield under your control, create a 2/2 white Cat creature token. If that enchantment is an Aura, you may attach it to the token"));
    }

    public AjanisChosen(final AjanisChosen card) {
        super(card);
    }

    @Override
    public AjanisChosen copy() {
        return new AjanisChosen(this);
    }
}

class AjanisChosenEffect extends OneShotEffect {

    public AjanisChosenEffect() {
        super(Outcome.PutCreatureInPlay);
        staticText = "create a 2/2 white Cat creature token. If that enchantment is an Aura, you may attach it to the token";
    }

    public AjanisChosenEffect(final AjanisChosenEffect effect) {
        super(effect);
    }

    @Override
    public AjanisChosenEffect copy() {
        return new AjanisChosenEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Token token = new CatToken();
            if (token.putOntoBattlefield(1, game, source.getSourceId(), source.getControllerId())) {
                Permanent enchantment = game.getPermanent(this.getTargetPointer().getFirst(game, source));
                if (enchantment != null && enchantment.hasSubtype(SubType.AURA, game)) {
                    for (UUID tokenId : token.getLastAddedTokenIds()) {
                        Permanent tokenPermanent = game.getPermanent(tokenId);
                        if (tokenPermanent != null) {
                            Permanent oldCreature = game.getPermanent(enchantment.getAttachedTo());
                            if (oldCreature != null && enchantment.getSpellAbility().getTargets().get(0).canTarget(tokenPermanent.getId(), game) && controller.chooseUse(Outcome.Neutral, "Attach " + enchantment.getName() + " to the token ?", source, game)) {
                                if (oldCreature.removeAttachment(enchantment.getId(), game)) {
                                    tokenPermanent.addAttachment(enchantment.getId(), game);
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

}
