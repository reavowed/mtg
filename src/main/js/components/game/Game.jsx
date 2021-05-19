import ActionManager from "../../ActionManager";
import DecisionMaker from "../../DecisionMaker";
import GameState from "../../GameState";
import GameDisplay from "./GameDisplay";

export default function Game() {
    return <GameState.Provider>
        <DecisionMaker.Provider>
            <ActionManager.Provider>
                <GameDisplay />
            </ActionManager.Provider>
        </DecisionMaker.Provider>
    </GameState.Provider>;
}
