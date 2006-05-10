/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/



package org.sakaiproject.tool.assessment.qti.helper;

import java.util.List;
import java.util.StringTokenizer;

import org.sakaiproject.tool.assessment.qti.constants.AuthoringConstantStrings;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentMetaDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Contract: use List of special "|" delimited "KEY|VALUE" Strings!
 * @author Ed Smiley esmiley@stanford.edu
 */
public class MetaDataList
{
  private static Log log = LogFactory.getLog(ExtractionHelper.class);

  /**
   * list of editable settings
   */
  private static final String[] editableKeys =
    {
    "assessmentAuthor_isInstructorEditable",
    "assessmentCreator_isInstructorEditable",
    "description_isInstructorEditable",
    "dueDate_isInstructorEditable",
    "retractDate_isInstructorEditable",
    "anonymousRelease_isInstructorEditable",
    "authenticatedRelease_isInstructorEditable",
    "ipAccessType_isInstructorEditable",
    "passwordRequired_isInstructorEditable",
    "timedAssessment_isInstructorEditable",
    "timedAssessmentAutoSubmit_isInstructorEditable",
    "itemAccessType_isInstructorEditable",
    "displayChunking_isInstructorEditable",
    "displayNumbering_isInstructorEditable",
    "submissionModel_isInstructorEditable",
    "lateHandling_isInstructorEditable",
    "autoSave_isInstructorEditable",
    "submissionMessage_isInstructorEditable",
    "finalPageURL_isInstructorEditable",
    "feedbackType_isInstructorEditable",
    "feedbackAuthoring_isInstructorEditable",
    "feedbackComponents_isInstructorEditable",
    "testeeIdentity_isInstructorEditable",
    "toGradebook_isInstructorEditable",
    "recordedScore_isInstructorEditable",
    "bgColor_isInstructorEditable",
    "bgImage_isInstructorEditable",
    "metadataAssess_isInstructorEditable",
    "metadataParts_isInstructorEditable",
    "metadataQuestions_isInstructorEditable",
  };

  private List metadataList;

  /**
   * Contract: use List of special "|" delimited "KEY|VALUE" Strings!
   * Uses special "|" delimited "KEY|VALUE" strings
   * @param metadataList
   */
  public MetaDataList(List metadataList)
  {
    this.setMetadataList(metadataList);
  }

  /**
   * Adds extraction-created list of "|" key value pairs
   * to item metadata map, if there are any.
   * Example:<br/>
   * <p>
     * &lt; metadata type =" list " &gt; TEXT_FORMAT| HTML &lt;/ metadata &gt;<br/> �
   * &lt; metadata type =" list " &gt; ITEM_OBJECTIVE| &lt/ metadata &gt;<br/>
   * Becomes:<br/>
   * TEXT_FORMAT=>HTML etc.
   * </p>
   * @param metadataList extraction-created list of "|" key value pairs
   * @param item the item
   */
  public void addTo(ItemDataIfc item)
  {
    if (metadataList == null)
    {
      return; // no metadata found
    }

    for (int i = 0; i < metadataList.size(); i++)
    {
      String meta = (String) metadataList.get(i);
      StringTokenizer st = new StringTokenizer(meta, "|");
      String key = null;
      String value = null;
      if (st.hasMoreTokens())
      {
        key = st.nextToken().trim();
      }
      if (st.hasMoreTokens())
      {
        value = st.nextToken().trim();
        item.addItemMetaData(key, value);
      }
    }
  }

  /**
   * Adds extraction-created list of "|" key value pairs
   * to assessment metadata map, if there are any.
   * Example:<br/>
   * <p>
   * &lt; metadata type =" list " &gt; FEEDBACK_SHOW_CORRECT_RESPONSE|True &lt;/ metadata &gt;<br/> �
   * &lt; metadata type =" list " &gt; FEEDBACK_SHOW_STUDENT_SCORE|True &lt/ metadata &gt;<br/>
   * Becomes:<br/>
   * TEXT_FORMAT=>HTML etc.
   * </p>
   * @param metadataList extraction-created list of "|" key value pairs
   * @param assessment the assessment
   */
  public void addTo(AssessmentFacade assessment)
  {
    if (metadataList == null)
    {
      return; // no metadata found
    }

    for (int i = 0; i < metadataList.size(); i++)
    {
      String meta = (String) metadataList.get(i);
      StringTokenizer st = new StringTokenizer(meta, "|");
      String key = null;
      String value = null;
      if (st.hasMoreTokens())
      {
        key = st.nextToken().trim();
      }

      // translate XML metadata strings to assessment metadata strings here
      // key to patch up the difference between Daisy's and earlier labels
      // that are compatible with the earlier beta version of Samigo
      if ("AUTHORS".equals(key))
      {
        key = AssessmentMetaDataIfc.AUTHORS;
      }
      if ("ASSESSMENT_KEYWORDS".equals(key))
      {
        key = AssessmentMetaDataIfc.KEYWORDS;
      }
      if ("ASSESSMENT_OBJECTIVES".equals(key))
      {
        key = AssessmentMetaDataIfc.OBJECTIVES;
      }
      if ("ASSESSMENT_RUBRICS".equals(key))
      {
        key = AssessmentMetaDataIfc.RUBRICS;
      }
      if ("BGCOLOR".equals(key))
      {
        key = AssessmentMetaDataIfc.BGCOLOR;
      }
      if ("BGIMG".equals(key))
      {
        key = AssessmentMetaDataIfc.BGIMAGE;
      }
      if ("COLLECT_ITEM_METADATA".equals(key))
      {
        key = "hasMetaDataForQuestions";

      }

      // for backwards compatibility with version 1.5 exports.
      if ("ASSESSMENT_RELEASED_TO".equals(key) &&
          value != null && value.indexOf("Authenticated Users") > -1)
      {
        log.debug(
          "Fixing obsolete reference to 'Authenticated Users', setting released to 'Anonymous Users'.");
        value = AuthoringConstantStrings.ANONYMOUS;
      }

      if (st.hasMoreTokens())
      {
        value = st.nextToken().trim();
        assessment.addAssessmentMetaData(key, value);
      }
    }
  }

  /**
   * Turns on editability for everything (ecept template info),
   * since we don't know if this  metadata is in the assessment or not,
   * or may not want to follow it, even if it is.
   *
   * The importer of the assesment may also be different than the
   * exporter, and may be on a different system or have different
   * templates, or policies, even if using this softwware.
   *
   * @param assessment
   */
  public void setDefaults(AssessmentFacade assessment)
  {
    // turn this off specially, as template settings are meaningless on import
    assessment.addAssessmentMetaData("templateInfo_isInstructorEditable",
                                     "false");

    for (int i = 0; i < editableKeys.length; i++)
    {
      assessment.addAssessmentMetaData(editableKeys[i], "true");
    }

  }

  public List getMetadataList()
  {
    return metadataList;
  }

  public void setMetadataList(List metadataList)
  {
    this.metadataList = metadataList;
  }

}
