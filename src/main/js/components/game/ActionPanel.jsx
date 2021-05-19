import {useContext} from "preact/hooks";
import GameState from "../../GameState";
import PriorityChoice from "./choices/PriorityChoice";

export default function ActionPanel() {
    const gameState = useContext(GameState);
    if (gameState.currentChoice.playerToAct !== gameState.player) {
        return "Waiting for opponent";
    }
    switch (gameState.currentChoice.type) {
        case "PriorityChoice":
            return <PriorityChoice />
    }
    return null;
}
