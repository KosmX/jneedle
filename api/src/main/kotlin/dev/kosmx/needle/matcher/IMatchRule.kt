package dev.kosmx.needle.matcher

import dev.kosmx.needle.matcher.result.IScanResult
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import software.coley.llzip.format.model.LocalFileHeader

/**
 * Represents a rule
 * The implementation should be effectively immutable and thread-safe
 * For every jar file, a matcher will be created
 */
fun interface IMatchRule {

    /**
     * Create a matcher for the specific jar
     * This method may validate ZipArchive but class checking shouldn't be done here.
     * @param context File context containing path and ZipFile. It may be used for extra validation or scanner delegation
     * @return JarMatcher or null if no checking is needed for this jar file
     */
    fun getJarMatcher(context: FileMatchContext): IJarMatcher?
}


/**
 * Mutable matcher type for specific JAR
 * [getClassMatcher] used to verify classes and create class checkers
 * Implementation may not be thread-safe, invocation order is strictly
 * 1. [IMatchRule.getJarMatcher] will be invoked for every matchRule, creating matchers for the specific jar. (this should be thread-safe)
 * 2. [IJarMatcher.getClassMatcher] will be invoked on instance
 * 3. [IClassMatcher.matchMethod] will be invoked on every method in the class (including <clinit> and <init>). The invocation order isn't specified
 * 4. [IJarMatcher.postClassMatch] will be invoked after all methods in the class are matched, ideally to process same-class conditions.
 * 5. [IJarMatcher.getMatchResult] will be invoked to get match result
 *
 * After step 1 and before step 5 [IJarMatcher.matchAsset] will be invoked on every asset. It doesn't have to be thread-safe but invocation order is not specified.
 *
 * A single class may implement [IJarMatcher] and [IClassMatcher] at the same time.
 * In this case [getClassMatcher] can be used to filter classes
 */
interface IJarMatcher {

    /**
     * Get checker object for specific class
     * @param clazz class to check
     * @return ClassMatcher or null to skip checking this class
     */
    fun getClassMatcher(clazz: ClassNode): IClassMatcher? = null

    /**
     * Invoked when all method is matched in current class
     */
    fun postClassMatch() = Unit

    /**
     * Match asset
     * @param assetHeader file header
     * @param asset lazy-evaluated asset data
     */
    fun matchAsset(assetHeader: LocalFileHeader, asset: Lazy<ByteArray>) = Unit

    /**
     * After every class is checked in the JAR, this function will return [IScanResult] or null if no matches were found
     * Conditions may be evaluated when this is invoked
     */
    fun getMatchResult(): IScanResult?
}

/**
 * match classes
 */
fun interface IClassMatcher {

    /**
     * Run match on instruction sequence. If matches, the corresponding [IJarMatcher] will return match results
     * @param method method object for optional validation
     * @param instructions method instruction sequence
     */
    fun matchMethod(method: MethodNode, instructions: Sequence<AbstractInsnNode>)
}

