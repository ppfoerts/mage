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
package mage.cards.l;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.DiesTriggeredAbility;
import mage.abilities.common.delayed.AtTheBeginOfNextEndStepDelayedTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.TransformAbility;
import mage.abilities.keyword.VigilanceAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.cards.u.UnhallowedCathar;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.permanent.Permanent;

/**
 *
 * @author BetaSteward
 */
public class LoyalCathar extends CardImpl {

    public LoyalCathar(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{W}{W}");
        this.subtype.add("Human");
        this.subtype.add("Soldier");

        this.transformable = true;
        this.secondSideCardClazz = UnhallowedCathar.class;

        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        this.addAbility(VigilanceAbility.getInstance());

        // When Loyal Cathar dies, return it to the battlefield transformed under your control at the beginning of the next end step.
        this.addAbility(new TransformAbility());
        this.addAbility(new DiesTriggeredAbility(new LoyalCatharEffect()));
    }

    public LoyalCathar(final LoyalCathar card) {
        super(card);
    }

    @Override
    public LoyalCathar copy() {
        return new LoyalCathar(this);
    }
}

class LoyalCatharEffect extends OneShotEffect {

    private static final String effectText = "return it to the battlefield transformed under your control at the beginning of the next end step";

    LoyalCatharEffect() {
        super(Outcome.Benefit);
        staticText = effectText;
    }

    LoyalCatharEffect(LoyalCatharEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        //create delayed triggered ability
        AtTheBeginOfNextEndStepDelayedTriggeredAbility delayedAbility = new AtTheBeginOfNextEndStepDelayedTriggeredAbility(new ReturnLoyalCatharEffect(source.getSourceId()));
        game.addDelayedTriggeredAbility(delayedAbility, source);
        return true;
    }

    @Override
    public LoyalCatharEffect copy() {
        return new LoyalCatharEffect(this);
    }

}

class ReturnLoyalCatharEffect extends OneShotEffect {

    private UUID cardId;

    public ReturnLoyalCatharEffect(UUID cardId) {
        super(Outcome.PutCardInPlay);
        this.cardId = cardId;
        this.staticText = "return it to the battlefield transformed under your control";
    }

    public ReturnLoyalCatharEffect(final ReturnLoyalCatharEffect effect) {
        super(effect);
        this.cardId = effect.cardId;
    }

    @Override
    public ReturnLoyalCatharEffect copy() {
        return new ReturnLoyalCatharEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card card = game.getCard(cardId);
        if (card != null) {
            card.putOntoBattlefield(game, Zone.GRAVEYARD, source.getSourceId(), source.getControllerId());
            Permanent perm = game.getPermanent(cardId);
            if (perm != null && perm.isTransformable()) {
                perm.transform(game);
                return true;
            }
        }
        return false;
    }

}
