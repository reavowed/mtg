import ActionManager from "../../contexts/ActionManager";
import CanvasManager from "../../contexts/CanvasManager";
import DecisionMaker from "../../contexts/DecisionMaker";
import GameState from "../../contexts/GameState";
import ObjectRefManager from "../../contexts/ObjectRefManager";
import StopsManager from "../../contexts/StopsManager";
import GameLayout from "./GameLayout";

export default function Game() {
    return <GameState.Provider>
        <DecisionMaker.Provider>
            <ActionManager.Provider>
                <StopsManager.Provider>
                    <ObjectRefManager.Provider>
                        <CanvasManager.Provider>
                            <GameLayout/>
                        </CanvasManager.Provider>
                    </ObjectRefManager.Provider>
                </StopsManager.Provider>
            </ActionManager.Provider>
        </DecisionMaker.Provider>
    </GameState.Provider>;
}
