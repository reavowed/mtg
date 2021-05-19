import {createContext} from "preact";
import {useCallback, useContext} from "preact/hooks";
import GameState from "./GameState";
import _ from "lodash";

const ActionManager = createContext(null);
const InternalProvider = ActionManager.Provider;

ActionManager.Provider = function({children}) {
    const gameState = useContext(GameState);
    const getActions = useCallback((objectId) => {
        if (!gameState.currentChoice || gameState.currentChoice.type !== "PriorityChoice" || gameState.currentChoice.playerToAct !== gameState.player)
            return [];
        if (_.includes(gameState.currentChoice.details.playableLands, objectId)) {
            return [{
                text: "Play",
                option: "Play " + objectId
            }];
        }
        return [];
    }, [gameState]);
    return <InternalProvider value={{getActions}}>{children}</InternalProvider>;
}

export default ActionManager;


