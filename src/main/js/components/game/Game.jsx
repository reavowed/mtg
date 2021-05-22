import ActionManager from "../../contexts/ActionManager";
import DecisionMaker from "../../contexts/DecisionMaker";
import GameState from "../../contexts/GameState";
import GameLayout from "./GameLayout";

export default function Game() {
    return <GameState.Provider>
        <DecisionMaker.Provider>
            <ActionManager.Provider>
                <GameLayout />
            </ActionManager.Provider>
        </DecisionMaker.Provider>
    </GameState.Provider>;
}
