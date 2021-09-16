import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import _ from "lodash";
import format from "date-fns/format"
import {commaList, getPlural} from "../../utils/word-helpers";

function getTargetDetails(targetNames) {
    return targetNames.length > 0 ? " targeting " + commaList(targetNames) : "";
}

function EventLogMessage({entry}) {
    switch (entry.type) {
        case "Start":
            return "Player " + entry.details.player + " begins the game."
        case "Mulligan":
            return "Player " + entry.details.player + " mulligans to " + getPlural(entry.details.newHandSize, "card", "cards") + ".";
        case "KeepHand":
            return "Player " + entry.details.player + " keeps a hand of " + getPlural(entry.details.handSize, "card", "cards") + ".";
        case "ReturnCardsToLibrary":
            return "Player " + entry.details.player + " puts " + getPlural(entry.details.numberOfCards, "card", "cards") + " on the bottom of their library.";
        case "NewTurn":
            return "Turn " + entry.details.turn.number + " (" + entry.details.turn.activePlayer + ")"
        case "DrawForTurn":
            return "Player " + entry.details.player + " draws for turn.";
        case "SkipFirstDrawStep":
            return "Player " + entry.details.player + " skips their first draw step.";
        case "PlayedLand":
            return "Player " + entry.details.player + " plays " + entry.details.landName + ".";
        case "CastSpell":
            return "Player " + entry.details.player + " casts " + entry.details.spellName + getTargetDetails(entry.details.targetNames) + ".";
        case "PutTriggeredAbilityOnStack":
            return "Player " + entry.details.player + " puts triggered ability of " + entry.details.sourceName + " on the stack" + getTargetDetails(entry.details.targetNames) + ". (" + entry.details.text + ")";
        case "ResolvePermanent":
            return "Player " + entry.details.player + " puts " + entry.details.permanentName + " onto the battlefield.";
        case "ResolveSpell":
            return "Player " + entry.details.player + " resolves " + entry.details.spellName + ".";
        case "ResolveAbility":
            return "Player " + entry.details.player + " resolves " + entry.details.typeDescription + " ability of " + entry.details.sourceName + ". (" + entry.details.text + ")";
        case "SpellFailedToResolve":
            return entry.details.spellName + " fails to resolve and is put into its owner's graveyard.";
        case "DeclareAttackers":
            return "Player " + entry.details.player + " attacks with " + commaList(entry.details.attackerNames) + ".";
        case "DeclareBlockers":
            return "Player " + entry.details.player + " blocks " + commaList(_.map(entry.details.blockerAssignments, (blockers, attacker) => attacker + " with " + commaList(blockers))) + ".";
        case "OrderBlockers":
            return "Player " + entry.details.player + " orders blockers for " + entry.details.attackerName + ": first " + entry.details.blockerNames.join(", then ") + ".";
        case "RevealCard":
            return "Player " + entry.details.player + " reveals " + entry.details.cardName + ".";;
        case "Scry":
            const clauses = [];
            entry.details.cardsOnTop && clauses.push(getPlural(entry.details.cardsOnTop, "card", "cards") + " to the top");
            entry.details.cardsOnBottom && clauses.push(getPlural(entry.details.cardsOnBottom, "card", "cards") + " to the bottom");
            return "Player " + entry.details.player + " scries " + commaList(clauses) + ".";
    }
}

function EventLogEntry({entry}) {
    const date = new Date(0)
    date.setUTCSeconds(entry.timestamp);

    return <div className="mb-2">
        <strong>{format(date, "HH:mm")}</strong>: <EventLogMessage entry={entry} />
    </div>
}

export default function EventLog() {
    const gameState = useContext(GameState);
    return <div className="px-1">
        {_.map(gameState.log, (entry, i) => <EventLogEntry key={i} entry={entry}/>)}
    </div>
}
