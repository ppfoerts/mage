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

import java.util.Iterator;
import java.util.UUID;
import mage.MageInt;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.common.BeginningOfEndStepTriggeredAbility;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.other.OwnerIdPredicate;
import mage.filter.predicate.permanent.TokenPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

/**
 *
 * @author LevelX2
 */
public class BroodingSaurian extends CardImpl {

    public BroodingSaurian(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{2}{G}{G}");
    this.subtype.add(SubType.LIZARD);

        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // At the beginning of each end step, each player gains control of all nontoken permanents he or she owns.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(new BroodingSaurianControlEffect(), TargetController.ANY, false));
    }

    public BroodingSaurian(final BroodingSaurian card) {
        super(card);
    }

    @Override
    public BroodingSaurian copy() {
        return new BroodingSaurian(this);
    }
}

class BroodingSaurianControlEffect extends ContinuousEffectImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent();
    static {
        filter.add(Predicates.not(new TokenPredicate()));
    }

    public BroodingSaurianControlEffect() {
        super(Duration.EndOfGame, Layer.ControlChangingEffects_2, SubLayer.NA, Outcome.GainControl);
        this.staticText = "each player gains control of all nontoken permanents he or she owns";
    }

    public BroodingSaurianControlEffect(final BroodingSaurianControlEffect effect) {
        super(effect);
    }

    @Override
    public BroodingSaurianControlEffect copy() {
        return new BroodingSaurianControlEffect(this);
    }

    @Override
    public void init(Ability source, Game game) {
        super.init(source, game);
        // add all creatures in range
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            for (UUID playerId : game.getState().getPlayersInRange(controller.getId(), game)) {
                FilterPermanent playerFilter = filter.copy();
                playerFilter.add(new OwnerIdPredicate(playerId));
                for (Permanent permanent :game.getBattlefield().getActivePermanents(playerFilter, playerId, game)) {
                    affectedObjectList.add(new MageObjectReference(permanent, game));
                }
            }
        }
    }

    @Override
    public boolean apply(Game game, Ability source) {
        for (Iterator<MageObjectReference> it = affectedObjectList.iterator(); it.hasNext();) { 
            Permanent creature = it.next().getPermanent(game);
            if (creature != null) {
                if (!creature.getControllerId().equals(creature.getOwnerId())) {
                    creature.changeControllerId(creature.getOwnerId(), game);
                }
            } else {
                it.remove();
            }
        }
        if (affectedObjectList.isEmpty()) {
            this.discard();
        }
        return true;
    }
}
