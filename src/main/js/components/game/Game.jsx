import ActionManager from "../../contexts/ActionManager";
import DecisionMaker from "../../contexts/DecisionMaker";
import GameState from "../../contexts/GameState";
import StopsManager from "../../contexts/StopsManager";
import GameLayout from "./GameLayout";

export default function Game() {
    return <GameState.Provider>
        <DecisionMaker.Provider>
            <ActionManager.Provider>
                <StopsManager.Provider>
                    <GameLayout/>
                </StopsManager.Provider>
            </ActionManager.Provider>
        </DecisionMaker.Provider>
    </GameState.Provider>;
}
