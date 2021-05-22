import {createContext} from "preact";
import {useCallback, useContext, useEffect, useState} from "preact/hooks";
import GameState from "./GameState";
import _ from "lodash";

const ActionManager = createContext(null);
const InternalProvider = ActionManager.Provider;

function getActionsFromGameState(gameState) {
    const actions = {};
    function addAction(objectId, action) {
        actions[objectId] = actions[objectId] || [];
        actions[objectId] = [...actions[objectId], action];
    }
    _.forEach(gameState.currentChoice.details.availableActions, action => addAction(action.objectId, {
        text: action.displayText,
        option: action.optionText
    }));
    return actions;
}

ActionManager.Provider = function({children}) {
    const gameState = useContext(GameState);
    const [actionsByObjectId, setActionsByObjectId] = useState(null);
    useEffect(() => {
        setActionsByObjectId(getActionsFromGameState(gameState))
    }, [gameState]);
    const getActions = useCallback((objectId) => {
        if (!gameState.currentChoice || gameState.currentChoice.type !== "PriorityChoice" || gameState.currentChoice.playerToAct !== gameState.player)
            return [];
        return actionsByObjectId[objectId] || [];
    }, [actionsByObjectId]);
    return actionsByObjectId && <InternalProvider value={{getActions}}>{children}</InternalProvider>;
}

export default ActionManager;


