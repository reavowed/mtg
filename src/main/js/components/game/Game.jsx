import ActionManager from "../../ActionManager";
import DecisionMaker from "../../DecisionMaker";
import GameState from "../../GameState";
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
