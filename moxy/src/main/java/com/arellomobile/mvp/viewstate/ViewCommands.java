package com.arellomobile.mvp.viewstate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.StateStrategy;

/**
 * Date: 17.12.2015
 * Time: 11:09
 *
 * @author Yuri Shmakov
 */
public class ViewCommands<View extends MvpView> {
	private List<ViewCommand<View>> mState = new ArrayList<>();
	private Map<Class<? extends StateStrategy>, StateStrategy> mStrategies = new HashMap<>();

	public void beforeApply(ViewCommand<View> viewCommand) {
		StateStrategy stateStrategy = getStateStrategy(viewCommand);

		stateStrategy.beforeApply(mState, viewCommand);
	}

	public void afterApply(ViewCommand<View> viewCommand) {
		StateStrategy stateStrategy = getStateStrategy(viewCommand);

		stateStrategy.afterApply(mState, viewCommand);
	}

	private StateStrategy getStateStrategy(ViewCommand<View> viewCommand) {
		StateStrategy stateStrategy = mStrategies.get(viewCommand.getStrategyType());
		if (stateStrategy == null) {
			//noinspection TryWithIdenticalCatches
			try {
				stateStrategy = viewCommand.getStrategyType().newInstance();
			} catch (InstantiationException e) {
				throw new IllegalArgumentException("Unable to create state strategy: " + viewCommand.toString());
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Unable to create state strategy: " + viewCommand.toString());
			}

			mStrategies.put(viewCommand.getStrategyType(), stateStrategy);
		}

		return stateStrategy;
	}

	public boolean isEmpty() {
		return mState.isEmpty();
	}

	public void reapply(View view) {
		final ArrayList<ViewCommand<View>> commands = new ArrayList<>(mState);

		for (ViewCommand<View> command : commands) {
			command.apply(view);

			afterApply(command);
		}
	}
}
