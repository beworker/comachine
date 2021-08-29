package de.halfbit.comachine.dsl

import de.halfbit.comachine.runtime.TransitionPerformedException
import kotlinx.coroutines.Job
import kotlin.reflect.KClass

@ComachineDsl
class OnEventBlock<State : Any, SubState : State>
@PublishedApi
internal constructor(
    private val getStateFct: () -> SubState,
    private val setStateFct: (SubState) -> Unit,
    @PublishedApi internal val extras: Lazy<MutableMap<KClass<*>, Any?>>,
    private val transitionToFct: (State) -> Unit,
    private val launchInStateFct: (LaunchBlockReceiver<State, SubState>) -> Job,
    private val launchInMachineFct: (LaunchBlockReceiver<State, SubState>) -> Job,
) {

    var state: SubState
        get() = getStateFct()
        set(value) {
            setStateFct(value)
        }

    inline fun <reified T> getExtra(): T {
        return extras.value[T::class] as T
    }

    inline fun <reified T> setExtra(value: T) {
        extras.value[T::class] = value
    }

    fun transitionTo(state: State): Nothing {
        transitionToFct(state)
        throw TransitionPerformedException()
    }

    fun launch(block: LaunchBlockReceiver<State, SubState>) =
        launchInStateFct(block)

    fun launchInMachine(block: LaunchBlockReceiver<State, SubState>) =
        launchInMachineFct(block)
}
