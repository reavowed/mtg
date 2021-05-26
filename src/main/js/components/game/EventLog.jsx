import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import _ from "lodash";
import format from "date-fns/format"
import {commaList, getPlural} from "../../utils/word-helpers";

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
            return "Turn " + entry.details.turnNumber + " (" + entry.details.player + ")"
        case "DrawForTurn":
            return "Player " + entry.details.player + " draws for turn.";
        case "SkipFirstDrawStep":
            return "Player " + entry.details.player + " skips their first draw step.";
        case "PlayedLand":
            return "Player " + entry.details.player + " plays " + entry.details.landName + ".";
        case "CastSpell":
            return "Player " + entry.details.player + " casts " + entry.details.spellName + ".";
        case "ResolvePermanent":
            return "Player " + entry.details.player + " resolves " + entry.details.permanentName + " and puts it onto the battlefield.";
        case "DeclareAttackers":
            return "Player " + entry.details.player + " attacks with " + commaList(entry.details.attackerNames) + ".";
        case "DeclareBlockers":
            return "Player " + entry.details.player + " blocks " + commaList(_.map(entry.details.blockerAssignments, (blockers, attacker) => attacker + " with " + commaList(blockers))) + ".";
    }
}

function EventLogEntry({entry}) {
    const date = new Date(0)
    date.setUTCSeconds(entry.timestamp);

    return <div className="mb-2">
        <strong>{format(date, "hh:mm")}</strong>: <EventLogMessage entry={entry} />
    </div>
}

export default function EventLog() {
    const gameState = useContext(GameState);
    return <div className="px-1">
        {_.map(gameState.log, (entry, i) => <EventLogEntry key={i} entry={entry}/>)}
    </div>
}
