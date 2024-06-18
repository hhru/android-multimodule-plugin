package ru.hh.android.plugin.inspections

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.ClassUtil
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.asJava.classes.KtUltraLightClass
import org.jetbrains.plugins.groovy.lang.resolve.processors.inference.type
import org.jetbrains.uast.UCallExpression
import kotlin.system.measureTimeMillis

@Suppress("UnstableApiUsage")
class PutSerializableDetector : Detector(), SourceCodeScanner {

    companion object {
        const val ISSUE_ID = "PutSerializableIssue"

        private const val CORRECT_PUT_SERIALIZABLE_ARGUMENTS_COUNT = 2
        private const val CHECKED_METHOD_NAME = "putSerializable"
        private const val FQCN_SERIALIZABLE_INTERFACE = "java.io.Serializable"

        val ISSUE: Issue
            get() {
                return Issue.create(
                    id = ISSUE_ID,
                    briefDescription = """
                    Lint rule for checking is `Bundle.putSerializable` invoking with correct Serializable argument.
                    """,
                    explanation = """
                    Lint rule for checking is `Bundle.putSerializable` invoking with correct Serializable argument.
                    
                    When you use current method you should be sure that every inner field in your argument, recursively,
                    implements $FQCN_SERIALIZABLE_INTERFACE .
                    """,
                    category = Category.LINT,
                    enabledByDefault = true,
                    severity = Severity.ERROR,
                    priority = 5,
                    implementation = Implementation(
                        PutSerializableDetector::class.java,
                        Scope.JAVA_FILE_SCOPE
                    )
                )
            }
    }

    private val logger = Logger.getInstance(PutSerializableDetector::class.java)

    override fun getApplicableMethodNames(): List<String>? {
        return listOf(CHECKED_METHOD_NAME)
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        logger.debug("----- visitMethodCall iteration -----")
        val evaluator = context.evaluator
        when {
            evaluator.isMemberInClass(method, SdkConstants.CLASS_BUNDLE).not() -> {
                logger.debug("\tevaluator.isMemberInClass(method, SdkConstants.CLASS_BUNDLE).not()")
                return
            }

            node.valueArgumentCount != CORRECT_PUT_SERIALIZABLE_ARGUMENTS_COUNT -> {
                logger.debug("\tnode.valueArgumentCount != 2")
                return
            }
        }

        val serializableArgumentReference = node.valueArguments[1].getExpressionType() as? PsiClassReferenceType ?: return
        logger.debug("\tserializableArgumentRef checked")
        val ktUltraLightClass = serializableArgumentReference.resolve() as? KtUltraLightClass ?: return
        logger.debug("\tktUltraLightClass checked")

        val checkedPsiClass = ClassUtil.findPsiClass(
            PsiManager.getInstance(context.project.ideaProject),
            FQCN_SERIALIZABLE_INTERFACE
        ) ?: return

        logger.debug("\tsuccessfully found java.io.Serializable PsiClass")

        if (ktUltraLightClass.isInheritor(checkedPsiClass, false).not()) {
            logger.debug("\targument class is not serializable")
            logger.debug("----- END need report issue -----")
            reportIssue(context, node, "${ktUltraLightClass.name}")
            return
        }

        var result: String? = null
        val checkTime = measureTimeMillis {
            result = ktUltraLightClass.allFields
                .filter {
                    it.type is PsiClassReferenceType &&
                        it.type.canonicalText.isNotBlank() &&
                        it.type.canonicalText.endsWith("Companion").not() &&
                        it.type.canonicalText.equals(ktUltraLightClass.type().canonicalText).not()
                }
                .mapNotNull { checkIsSerializable(it.type as PsiClassReferenceType, checkedPsiClass, "${ktUltraLightClass.name}") }
                .firstOrNull()
        }
        logger.debug(
            "\tChecked all inner fields for inheritance, is fully serializable: " +
                "is serializable ${result == null} [time: $checkTime ms]"
        )

        result?.let { pathToFirstNotSerializableObject ->
            logger.debug("----- END need report issue -----")
            reportIssue(context, node, pathToFirstNotSerializableObject)
        }
    }

    private fun checkIsSerializable(
        psiClassReferenceType: PsiClassReferenceType,
        checkedClass: PsiClass,
        objectPath: String
    ): String? {
        logger.debug("\t\ttry to check | canonicalText: ${psiClassReferenceType.canonicalText}")

        val ktLightClassForSourceDeclaration = psiClassReferenceType.resolve() as? KtLightClassForSourceDeclaration
        if (ktLightClassForSourceDeclaration == null) {
            logger.debug("\t\t\t it is not ref to kotlin class --> return true, i can't check it right now")
            return null
        }

        logger.debug("\t\t\t it is ref to kotlin class --> deep check")
        val isInheritor = ktLightClassForSourceDeclaration.isEnum || ktLightClassForSourceDeclaration.isInheritor(checkedClass, false)
        if (!isInheritor) {
            logger.debug("\t\t\t this ref already is not Serializable --> return false")
            return "$objectPath.${ktLightClassForSourceDeclaration.name}"
        }

        logger.debug("\t\t\t this ref is Serializable --> check every ref field")
        return ktLightClassForSourceDeclaration.allFields
            .filter {
                it.type is PsiClassReferenceType &&
                    it.type.canonicalText.isNotBlank() &&
                    it.type.canonicalText.endsWith("Companion").not() &&
                    it.type.canonicalText.equals(ktLightClassForSourceDeclaration.type().canonicalText).not()
            }
            .mapNotNull {
                checkIsSerializable(
                    it.type as PsiClassReferenceType,
                    checkedClass,
                    "$objectPath.${ktLightClassForSourceDeclaration.name}"
                )
            }
            .firstOrNull()
    }

    private fun reportIssue(
        context: JavaContext,
        node: UCallExpression,
        pathToFirstNotSerializableObject: String
    ) {
        context.report(
            issue = ISSUE,
            location = context.getLocation(node),
            message = "Argument is not fully Serializable! Check it and its inner fields! " +
                "[First not serializable class name: $pathToFirstNotSerializableObject]"
        )
    }
}
