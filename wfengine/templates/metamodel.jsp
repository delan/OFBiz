<%@page import="java.util.*";%>
<%@page import="ru.novosoft.uml.*";%>
<%@page import="ru.novosoft.uml.foundation.core.*";%>
<%@page import="ru.novosoft.uml.foundation.data_types.*";%>
<%@page import="ru.novosoft.uml.foundation.extension_mechanisms.*";%>
<%@page import="ru.novosoft.uml.model_management.*";%>
<%@page import="ru.novosoft.uml.behavior.common_behavior.*";%>
<%@page import="ru.novosoft.uml.behavior.use_cases.*";%>
<%@page import="ru.novosoft.uml.behavior.state_machines.*";%>
<%@page import="ru.novosoft.uml.behavior.activity_graphs.*";%>
<%!
	/**
	 * Dieser String enthält alle packages, die bei der Ermittlung des Namespaces igoriert werden
	 * sollen.
	 */
	public  String IGNOREABLE_PACKAGES="Einfaches_Modell_java_<global>";
	/**
	 * Bezeichnung für den Tag, welcher die Dokumentation enthält. Dies ist je nach CASE-Tool
	 * unterschiedlich. Bei RR ist es "documentation"
	 */
	public  String DOC_TAG="documentation";
	public  String PERSISTENCE_TAG="RationalRose$Java:Transient";
	public  String NAME_PREFIX="";
	
	

	/**
	 * Ermittelt das übergeordnete Element von m
	 * @param m Untergeordnetes Element
	 * @return Übergeordnetes Element von m
	 */
	public  MBase getParent(MBase m)  {
		return m!=null ? m.getModelElementContainer() : null;
	}

	/**
	 * Ermittelt die untergeordneten Elemente von m
	 * @param m Übergeordnetes Element
	 * @return Untergeordneten Elemente von m
	 */
	public  Collection getChilds(MBase m)  {
			Collection c=getStaticChilds(m);

			if (m instanceof MTransition)  {
				//c.add(((MTransition)m).getSource());
				c.add(((MTransition)m).getTarget());
				c.add(((MTransition)m).getTrigger());
				c.add(((MTransition)m).getEffect());
				c.add(((MTransition)m).getGuard());
				c.add(((MTransition)m).getState());
			}

			if (m instanceof MStateMachine)  {
				MStateMachine clsf = (MStateMachine)m;
				c.addAll(clsf.getTransitions());
			}
			
			if (m instanceof MState)  {
				c.add(((MState)m).getDoActivity());
				c.add(((MState)m).getEntry());
				c.add(((MState)m).getExit());
			}

			if (m instanceof MActionSequence)  {
				c.addAll(((MActionSequence)m).getActions());
			}
			
			if (m instanceof MActivityGraph)  {
				MActivityGraph clsf = (MActivityGraph)m;
				c.addAll(clsf.getPartitions());
			}
			
			c.addAll(getComponents(m));
			return c;
	}

	/**
	 * Ermittelt die statischen untergeordneten Elemente von m 
	 * @param m Übergeordnetes Element
	 * @return Untergeordneten Elemente von m
	 */
	public  Collection getStaticChilds(MBase m)  {
			Collection c=new ArrayList();
			if (m instanceof MClassifier)  {
				MClassifier clsf = (MClassifier)m;
				c.addAll(clsf.getFeatures());
			}

			if (m instanceof MNamespace)  {
				c.addAll( ((MNamespace)m).getOwnedElements());
			}

			if (m instanceof MPackage)  {
				c.addAll( ((MPackage)m).getElementImports());
			}

			
			c.addAll(getComponents(m));
			return c;
	}
	
	/**
	 * Ermittelt die untergeordneten Elemente von m. Diese Methode ist für die graphische
	 * Darstellung gedacht.
	 * @param m Übergeordnetes Element
	 * @return Untergeordneten Elemente von m
	 */
	public  Collection getAllChilds(MBase m)  {
		
			Collection c=getChilds(m);
			/*
			if (m instanceof MModelElement)  {
				MModelElement me = (MModelElement)m;
				//c.addAll(me.getPartitions1());
				//c.addAll(me.getBindings());
				//c.addAll(me.getTaggedValues());
			}*/
			
			if (m instanceof MClassifier)  {
				MClassifier clsf = (MClassifier)m;
				c.addAll(clsf.getAssociationEnds());
				c.addAll(clsf.getParticipants());
				c.addAll(clsf.getObjectFlowStates());
			}

			if (m instanceof MBehavioralFeature)  {
				MBehavioralFeature clsf = (MBehavioralFeature)m;
				c.addAll(clsf.getParameters());
				c.addAll(clsf.getRaisedSignals());
			}

			if (m instanceof MFeature)  {
				MFeature clsf = (MFeature)m;
				c.addAll(clsf.getClassifierRoles());
			}

			if (m instanceof MActivityGraph)  {
				MActivityGraph clsf = (MActivityGraph)m;
				c.addAll(clsf.getPartitions());
			}

			if (m instanceof MStateMachine)  {
				MStateMachine clsf = (MStateMachine)m;
				c.addAll(clsf.getTransitions());
			}

			if (m instanceof MUseCase)  {
				c.addAll(((MUseCase)m).getIncludes());
				c.addAll(((MUseCase)m).getExtends());
			}
			if (m instanceof MSignal)  {
				c.addAll(((MSignal)m).getContexts());
			}
			if (m instanceof MTransition)  {
				c.add(((MTransition)m).getSource());
				c.add(((MTransition)m).getTarget());
				c.add(((MTransition)m).getTrigger());
				c.add(((MTransition)m).getEffect());
				c.add(((MTransition)m).getGuard());
				c.add(((MTransition)m).getState());

			}

			

			if (m instanceof MPartition)  {
				c.addAll(((MPartition)m).getContents());
			}
			return c;
	}
	/**
	 * Ermittelt die zugehörigen Komponenten des Elements
	 * @return Alle Komponenten, in denen das Element benötigt wird
	 */
	public  Collection getComponents(MBase m)  {
		Collection c = new ArrayList();
		if (m instanceof MModelElement)  {
			MModelElement e = (MModelElement)m;
			Collection deps = e.getElementResidences();
			Iterator it = deps.iterator();
			while (it.hasNext())  {
				MBase elem = (MBase)it.next();
				if (elem instanceof MElementResidence)  {
					c.add(((MElementResidence)elem).getImplementationLocation());
				}
			}
		}
		return c;
	}
	
	/**
	 * Liefert alle Klassen und Schnittstellen in einem Package
	 */
	public  Collection getClassifierOfPackage(MBase pPackage)  {
		return pPackage!=null ? filter(getChilds(pPackage), MClassifier.class) : Collections.EMPTY_LIST;
	}

	/**
	 * Ermittelt den zugeordneten Namesraum (Package) eines Elements.
	 * @param m Element
	 * @see getNamespaceName
	 * @return Zugeordneten Namesraum oder null, wenn kein Namesraum vorhanden
	 */
	public  MBase getNamespace(MBase m)  {
		if (m==null) return null;
		
		if (m instanceof  MNamespace) return getParent(m);
		if (m instanceof  MModelElement) return ((MModelElement)m).getNamespace();
		return null;
	}

	/**
	 * Ermittelt den vollständigen Pfad (Packagenamen) eines Elements. Die Namensräume werden
	 * mit '.' getrennt.
	 * @param m Element
	 * @return vollständigen Pfad
	 */
	public  String getNamespaceName(MBase m)  {
		return getNamespaceName(m, '.');
	}

	/**
	 * Ermittelt den vollständigen Pfad (Packagenamen) eines Elements. Die Namensräume werden
	 * (je nach Betriebssystem) mit '\\' oder '/' getrennt.
	 * @param m Element
	 * @return vollständigen Pfad
	 */
	public  String getPath(MBase m)  {
		return getNamespaceName(m, File.separatorChar);
	}

	/**
	 * Ermittelt den vollständigen Pfad (Packagenamen) eines Elements
	 * @param m Element
	 * @param pSeparator Trennzeichen
	 * @return vollständigen Pfad
	 */
	public  String getNamespaceName(MBase m, char pSeparator)  {
		MBase ns = getNamespace(m);
		Stack st=new Stack();

		while (ns!=null)  {
			String nsname = getName(ns);
			// Package ignorierbar?
			if (!(ns instanceof MModel))  {
				st.push(ns);
			}
			ns = getNamespace(ns);
		}

		String ret="";
		while (!st.isEmpty())  {
			if (ret.length()==0)  {
				ret += fd(getName((MBase)st.pop()));
			} else  {
				ret += pSeparator+fd(getName((MBase)st.pop()));
			}
		}
		//System.out.println("nn="+ret+"/"+getName(m));
		return ret.startsWith(".") ? ret.substring(1) : ret ;
	}

	/**
	 * Liefert den Namen eines Elements
	 * @param m Element
	 * @return Name von m
	 */
	public  String getName(MBase m)  {
		// owl: Workaround für UML-Generierung
		if (m instanceof MClass || m instanceof MInterface || m instanceof MException)  {
			return NAME_PREFIX+((MModelElement)m).getName();
		}
		return (m instanceof MModelElement) ? ((MModelElement)m).getName() :
			(m!=null ? "("+m.toString()+" has no name!)" : "NULL");
	}

	/**
	 * Wie getName, aber der erste Buchstabe wir in einen Großbuchstaben konvertiert.
	 * @param m Element
	 * @return Name des Elements
	 */
	public  String getNameFU(MBase m)  {
		return fu(getName(m));
	}

	/**
	 * Wie getName, aber der erste Buchstabe wir in einen Kleinbuchstaben konvertiert.
	 * @param m Element
	 * @return Name des Elements
	 */
	public  String getNameFD(MBase m)  {
		return fd(getName(m));
	}

	/**
	 * Liefert den vollqualifizierten Namen eines Elements (z. B. "org.foo.bar.Baz")
	 * @param m Element
	 * @return Vollqualifizierter Name des Elements
	 */
	public  String getFQName(MBase m)  {
		String tmp = getNamespaceName(m);
		return (tmp!=null && tmp.length()>0  ? tmp+"." : "")+getName(m);
	}

	/**
	 * Liefert die eindeutige ID des Elements
	 * @param m Element
	 * @return UUID des Elements
	 */
	public  String getId(MBase m)  {
		return m!=null ? m.getUUID() : null;
	}

	/**
	 * Liefert den Initialwert eines Attributes
	 * @param m Element
	 * @return Initialwert des Attributs oder null, wenn nicht gesetzt
	 */
	public  String getInitialValue(MBase m)  {
		if (isAttribute(m))  {
				MExpression ex = ((MAttribute)m).getInitialValue();
				if (ex!=null)  {
					return ex.getBody();
				}
		}
		return null;
	}

	/**
	 * Liefert den Stereotypen des Elements. Wenn das Element keinen Stereotypen hat oder
	 * haben kann, wird ein Leerstring zurückgegeben.
	 * @param m Element
	 * @return Name des Elements
	 */
	public  String getStereotype(MBase m)  {
		if (m instanceof MModelElement)  {
			MModelElement ns = (MModelElement)m;
			MStereotype s = ns.getStereotype();
			return s!=null ? getName(s) : "";
		}
		return "";
	}

	/**
	 * Liefert die Beschreibung zu einem Element
	 * .@return Beschreibung des Elements, wenn vorhanden
	 */
	public  String getDocumentation(MBase m) {
		MTaggedValue tv = (MTaggedValue)getTaggedValue(m, DOC_TAG);
		return tv!=null ? tv.getValue() : "";
	}

	/**
	 * Liefert die Parameterliste einer Methode einschließlich Rückgabeparameter
	 * @param m Element
	 * @return Parameterliste
	 */
	public  Collection getAllParameters(MBase m)  {
		if (m instanceof MBehavioralFeature)  {
			MBehavioralFeature bv =(MBehavioralFeature)m;
			return bv.getParameters();
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * Liefert die Parameterliste einer Methode
	 * @param m Element
	 * @return Parameterliste
	 */
	public  Collection getParameterList(MBase m)  {
		if (m instanceof MBehavioralFeature)  {
			MBehavioralFeature bv =(MBehavioralFeature)m;
			Collection c = bv.getParameters();
			Collection ret = new ArrayList();
			Iterator it = c.iterator();
			while (it.hasNext())  {
				MParameter par = (MParameter)it.next();
				if (par.getKind().getValue()!=MParameterDirectionKind._RETURN)  {
					ret.add(par);
				}
			}
			return ret;
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * Liefert die Liste der Parametertypen einer Methode
	 * @param m Element
	 * @return Parametertypenliste
	 */
	public  Collection getParameterTypeList(MBase m)  {
		if (m instanceof MOperation)  {
			MOperation opp =(MOperation)m;
			Collection c = opp.getParameters();
			Collection ret = new ArrayList();
			Iterator it = c.iterator();
			while (it.hasNext())  {
				MParameter par = (MParameter)it.next();
				if (par.getKind().getValue()!=MParameterDirectionKind._RETURN)  {
					ret.add(getName(par.getType()));
				}
			}
			return ret;
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * Liefert die Parameterliste einer Methode als Zeichenkette
	 * @param m Element
	 * @return Parameterliste
	 */
	public  String getParameterListAsString(MBase m)  {
		if (m instanceof MBehavioralFeature)  {
			MBehavioralFeature bv =(MBehavioralFeature)m;
			Collection c = bv.getParameters();
			StringBuffer ret = new StringBuffer();
			Iterator it = c.iterator();
			boolean first = true;
			while (it.hasNext())  {
				MParameter par = (MParameter)it.next();

				if (par.getKind().getValue()!=MParameterDirectionKind._RETURN)  {
					if (!first)  {
						ret.append(", ");
					}
					ret.append(getName(par.getType())).append(" ").append(getName(par));
				}
				first = false;
			}
			return ret.toString();
		}
		return "";
	}

	/**
	 * Liefert die Exceptions einer Methode als Liste
	 * @param m Element
	 * @return Liste der geworfenen Exceptions
	 */
	 public  Collection getExceptions(MBase m)  {
	 	if (m instanceof MOperation)  {
	 		MOperation op =(MOperation)m;
	 		return op.getRaisedSignals();
	 	}
		return Collections.EMPTY_LIST;
	 }

	/**
	 * Liefert die Exceptions einer Methode als Zeichenkette
	 * @param m Element
	 * @return Exceptions als Zeichenkette
	 */
	public  String getExceptionListAsString(MBase m)  {
		StringBuffer ret = new StringBuffer("");
		Collection c = getExceptions(m);
		Iterator it = c.iterator();
		boolean first = true;
		while (it.hasNext())  {
			if (!first)  {
				ret.append(", ");
			} else  {
				ret.append(" throws ");
			}
			MException par = (MException)it.next();
			// WORKAROUND: In Rose gibt es für Exceptions keinen echten Namespace, daher
			// müssen sie im throws-Feld vollqualifiziert eingegegeben werden. Leider müssen
			// wir hier dann auch die Qualifizierung entfernen
			ret.append(unqualify(getName(par), '.'));
			first = false;
		}
		if (c.size() > 0) ret.append(" ");
		return ret.toString();
	}

	/**
	 * Liefert den Rückgabetyp einer Methode
	 * @param m Element
	 * @return Rückgabetyp
	 */
	public  MClassifier getReturnType(MBase m)  {
		if (m instanceof MBehavioralFeature)  {
			MBehavioralFeature bv =(MBehavioralFeature)m;
			Collection c = bv.getParameters();
			Iterator it = c.iterator();
			while (it.hasNext())  {
				MParameter par = (MParameter)it.next();
				if (par.getKind().getValue()==MParameterDirectionKind._RETURN)  {
					return par.getType();
				}
			}
			return null;
		}
		return null;
	}


	/**
	 * Liefert den Rückgabetyp einer Methode
	 * @param m Element
	 * @return Name des Rückgabetyps
	 */
	public  String getReturnTypename(MBase m)  {
		return getReturnType(m)!=null ? getName(getReturnType(m)) : "void";
	}

 	/**
	 * Liefert alle Attribute eines Elements
	 * @param m Element
	 * @return Attributliste
	 */
	public  Collection getAttributes(MBase m)  {
		return filter(getChilds(m), MAttribute.class);
	}

	/**
	 * Liefert alle Methoden eines Elements sowie die Methode der implementierten
	 * Schnittstellen (und irgdendwann mal die abstrakten Methoden der Basisklasse)
	 * @param m Element
	 * @return Methodenliste
	 */
	public  Collection getOperations(MBase m)  {
		Collection ret = new ArrayList();
		ret.addAll(filter(getChilds(m), MOperation.class));
		if (m instanceof MClass)  {
			Collection realizedMethods = getInterfaces(m);
			Iterator it = realizedMethods.iterator();
			while (it.hasNext())  {
				MBase iface = (MBase)it.next();
				ret.addAll(getOperations(iface));
			}
		}
		return ret;
	}
	
	/**
	 * Liefert alle Methoden eines Elements
	 * @param m Element
	 * @return Methodenliste
	 */
	public  Collection getOwnOperations(MBase m)  {
		Collection ret = new ArrayList();
		ret.addAll(filter(getChilds(m), MOperation.class));	
		return ret;
	}

	/**
	 * Liefert alle Assoziationen eines Elements
	 * @param m Element
	 * @return Methodenliste
	 */
	public  Collection getAssociations(MBase m)  {
		Collection ret = (m!=null && m instanceof MClassifier) ? 
			new ArrayList(((MClassifier)m).getAssociationEnds()) : 
			new ArrayList(Collections.EMPTY_LIST);
		if (m instanceof MClass)  {
			Collection realizedMethods = getInterfaces(m);
			Iterator it = realizedMethods.iterator();
			while (it.hasNext())  {
				MBase iface = (MBase)it.next();
				//System.out.println("Adding associations from "+iface);
				ret.addAll(getAssociations(iface));
			}
		}
		return ret;
		/*
		Collection c= (m!=null && m instanceof MClassifier) ? ((MClassifier)m).getAssociationEnds() : null;
		Collection ret = new ArrayList();
		Iterator it = c.iterator();
		while (it.hasNext())  {
			MAssociationEnd here = (MAssociationEnd)it.next();
			MAssociationEnd opp = here.getOppositeEnd();
			String oppName = getName(opp);
			if (oppName.length()>0)  {
				ret.add(opp);
			} else  {
				ret.add(here);
			}
		}
		
		return ret;
		*/
	}


	/**
	 * Liefert den unteren Wert der Kardinalität
	 * @param m Kardinalitäten
	 */
	public  int getMultLower(MMultiplicity m)	{
		if (null == m){
		  return 0;
		}
		return m.getLower();
	}

	/**
	 * Liefert den oberen Wert der Kardinalität
	 * @param m Kardinalitäten
	 */
	public  int getMultUpper(MMultiplicity m)	{
		if (null == m) {
		  return MMultiplicity.N;
		}
		return m.getUpper();
	}

	/**
	 * Liefert alle "Tagged Values" eines Elements
	 * @param m Element
	 * @return Liste der Tagged Values
	 */
	public  Collection getTaggedValues(MBase m)  {
		return m instanceof MModelElement ? ((MModelElement)m).getTaggedValues() : Collections.EMPTY_LIST;
	}

	/**
	 * Liefert eine bestimmte Tagged Value
	 * @param m Element
	 * @param tag Name des Tags
	 * @return Tagged Values mit Tag 'tag' oder null, wenn nicht vorhanden
	 */
	public  MBase getTaggedValue(MBase m, String tag) {
		Collection c = getTaggedValues(m);
		Iterator it = c.iterator();
		while(it.hasNext()) {
			MTaggedValue tv = (MTaggedValue)it.next();			
			if (tv.getTag().equalsIgnoreCase(tag)) {
				return tv;
			}
		}
		return null;
	}
	
	public  Collection getAllPackages(MBase root)  {
		return filterRecursive(new ArrayList(), root, MPackage.class);
	}

	public  Collection getAllClasses(MBase root)  {
		return filterRecursive(new ArrayList(), root, MClassifier.class);
	}
	
	/**
	 * Liefert eine bestimmte Tagged Value
	 * @param m Element
	 * @param tag Name des Tags
	 * @return Wert des Tags 'tag' oder null, wenn nicht vorhanden
	 */
	public  String getTaggedValueAsString(MBase m, String tag) {
		MTaggedValue tv= (MTaggedValue)getTaggedValue(m, tag);
		return tv!=null ? tv.getValue() : null;
	}

	private  Collection filter(Collection list, Class c)  {
		if (list==null ) return null;
		String name = c.getName();
		List result = new ArrayList();
		Iterator it = list.iterator();
		while (it.hasNext())  {
			Object o = it.next();
			if (c.isInstance(o)) result.add(o);
		}
		return result;
	}
	
	private  Collection filterRecursive(Collection result, MBase pRoot, Class c)  {
		if (pRoot==null) return result;
		
		Collection childs = getChilds(pRoot);
		result.addAll(filter(childs, c));
		for(Iterator it = childs.iterator(); it.hasNext();)  {
			filterRecursive(result, (MBase)it.next(),c);
		}
		return result;
	}

	/**
	 * Prüft, ob des übergebene Element eine Klasse ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Klasse ist
	 */
	public  boolean isClass(MBase m)  {
		return m!=null && m instanceof MClass;
	}

	/**
	 * Prüft, ob des übergebene Element eine Exception ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Exception ist
	 */
	public  boolean isException(MBase m)  {
		return m!=null && m instanceof MException;
	}
	
	/**
	 * Prüft, ob des übergebene Element ein Attribut ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Attribut ist
	 */
	public  boolean isAttribute(MBase m)  {
		return m!=null && m instanceof MAttribute;
	}

	/**
	 * Prüft, ob des übergebene Element ein Attribut ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Attribut ist
	 */
	public  boolean isOperation(MBase m)  {
		return m!=null && m instanceof MOperation;
	}

	/**
	 * Prüft, ob des übergebene Element eine Komponente ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Attribut ist
	 */
	public  boolean isComponent(MBase m)  {
		return m!=null && m instanceof MComponent;
	}

	/**
	 * Prüft, ob des übergebene Element ein Zustandsübergang  ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Zustandsübergang ist
	 */
	public  boolean isTransition(MBase m)  {
		return m!=null && m instanceof MTransition;
	}

	/**
	 * Prüft, ob des übergebene Element ein Zustand  ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Zustand ist
	 */
	public  boolean isStateVertex(MBase m)  {
		return m!=null && m instanceof MStateVertex;
	}

	/**
	 * Prüft, ob des übergebene Element ein Zustand  ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Zustand ist
	 */
	public  boolean isState(MBase m)  {
		return m!=null && m instanceof MState;
	}

	/**
	 * Prüft, ob des übergebene Element eine Aktion ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Aktion ist
	 */
	public  boolean isAction(MBase m)  {
		return m!=null && m instanceof MAction;
	}

	/**
	 * Prüft, ob des übergebene Element eine Aktionsfolge ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Aktionsfolge ist
	 */
	public  boolean isActionSequence(MBase m)  {
		return m!=null && m instanceof MActionSequence;
	}

	/**
	 * Prüft, ob des übergebene Element eine Aktion ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Aktion ist
	 */
	public  boolean isActionState(MBase m)  {
		return m!=null && m instanceof MActionState;
	}

	/**
	 * Prüft, ob des übergebene Element eine Pseudo-Aktion ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Pseudo-Aktion ist
	 */
	public  boolean isPseudostate(MBase m)  {
		return m!=null && m instanceof MPseudostate;
	}

	/**
	 * Prüft, ob des übergebene Element eine Pseudo-Aktion ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Pseudo-Aktion ist
	 */
	public 	boolean isObjectFlowState(MBase m)  {
		return m!=null && m instanceof MObjectFlowState;
	}
	
	public String getObjectStates(MBase m) {
		if (!isObjectFlowState(m)) return null;
		MObjectFlowState ofs = (MObjectFlowState)m;
		MClassifier c= ofs.getType();
		if (c!=null && c instanceof MClassifierInState) {
			System.out.println("Found type "+c);
			MClassifierInState cis = (MClassifierInState)c;
			String ret="";
			for(Iterator it =cis.getInStates().iterator(); it.hasNext();) {
				MBase x = (MBase)it.next();
				ret += getName(x);
				if (it.hasNext()) ret+=",";
				System.out.println("state: " +ret);
			}
			return ret;
		} else {
			System.out.println("No type in "+m);
		}
		return null;
	}
	
	/**
	 * Liefert den Typ der Pseudo-Aktion
	 * @param m Zu prüfendes Element
	 * @return Art der Aktion oder -1, wenn ungültiges Element
	 */
	public  int getPseudostateKind(MBase m)  {
		if (m!=null && m instanceof MPseudostate)  {
			if (((MPseudostate)m).getKind()==null) {
				System.out.println("Pseudostate without kind found!");
				return -1;
			}
			return ((MPseudostate)m).getKind().getValue();
		}
		return -1;
	}

	/**
	 * Liefert den Typ der Pseudo-Aktion
	 * @param m Zu prüfendes Element
	 * @return Art der Aktion oder -1, wenn ungültiges Element
	 */
	public  MBase getInitialState(MBase m)  {
		if (m!=null && m instanceof MActivityGraph)  {
			return ((MActivityGraph)m).getTop();
		}
		return null;
	}

	/**
	 * Liefert den Typ der Pseudo-Aktion im Textformat
	 * @param m Zu prüfendes Element
	 * @return Art der Aktion
	 */
	public  String getPseudostateKindVerbose(MBase m)  {
		int kind = getPseudostateKind(m);
		String ret;
		switch (kind)  {
			case MPseudostateKind._FINAL:
				return "(End)";
			case MPseudostateKind._INITIAL:
				return "(Start)";
			case MPseudostateKind._BRANCH:
				return "(Branch)";
			case MPseudostateKind._JOIN:
				return "(Join)";
			case MPseudostateKind._JUNCTION:
				return "(Junction)";
			case MPseudostateKind._SHALLOW_HISTORY:
				return "(Shallow history)";
			case MPseudostateKind._DEEP_HISTORY:
				return "(Deep history)";
			default:
				return "?";
		}
	}

	/**
	 * Prüft, ob des übergebene Element ein Startpunkt ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Startpunkt ist
	 */
	public  boolean isInitialState(MBase m)  {
		return getPseudostateKind(m)==MPseudostateKind._INITIAL;
	}

	/**
	 * Prüft, ob des übergebene Element ein Join ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Join ist
	 */
	public  boolean isJoin(MBase m)  {
		return getPseudostateKind(m)==MPseudostateKind._JOIN;
	}
	
	/**
	 * Prüft, ob des übergebene Element ein Join ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Join ist
	 */
	public  boolean isBranch(MBase m)  {
		return getPseudostateKind(m)==MPseudostateKind._BRANCH;
	}
	
	/**
	 * Prüft, ob des übergebene Element ein Endpunkt ist (hier gibt es zewi Möglichkeiten)
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Endpunkt ist
	 */
	public  boolean isFinalState(MBase m)  {
		return (m!=null && m instanceof MFinalState) || getPseudostateKind(m)==MPseudostateKind._FINAL;
	}

	/**
	 * Prüft, ob des übergebene Element ein ? ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine ? ist
	 */
	public  boolean isFlowState(MBase m)  {
		return (m!=null && m instanceof MObjectFlowState);
	}
	
	/**
	 * Prüft, ob des übergebene Element ein Parameter ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Parameter ist
	 */
	public  boolean isParameter(MBase m)  {
		return m!=null && m instanceof MParameter;
	}

	/**
	 * Prüft, ob des übergebene Element ein Package ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Package ist
	 */
	public  boolean isPackage(MBase m)  {
		return m!=null && m instanceof MPackage;
	}

	/**
	 * Prüft, ob des übergebene Element ein Interface ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Interface ist
	 */
	public  boolean isInterface(MBase m)  {
		return m!=null && m instanceof MInterface;
	}

	/**
	 * Prüft, ob des übergebene Element eine Abstraktion ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Abstraktion ist
	 */
	public  boolean isAbstraction(MBase m)  {
		return m!=null && m instanceof MAbstraction;
	}

	/**
	 * Prüft, ob des übergebene Element eine Abhängigkeitsbeziehung ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Abhängigkeitsbeziehung ist
	 */
	public  boolean isDependency(MBase m)  {
		return m!=null && m instanceof MDependency;
	}

	/**
	 * Prüft, ob des übergebene Element ein Aktivitätsdiagramm ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Aktivitätsdiagramm ist
	 */
	public  boolean isActivityGraph(MBase m)  {
		return m!=null && m instanceof MActivityGraph;
	}

	/**
	 * Prüft, ob des übergebene Element ein Use Case ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Use Case ist
	 */
	public  boolean isUseCase(MBase m)  {
		return m!=null && m instanceof MUseCase;
	}

	/**
	 * Prüft, ob des übergebene Element ein Actor ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Actor ist
	 */
	public  boolean isActor(MBase m)  {
		return m!=null && m instanceof MActor;
	}

	/**
	 * Prüft, ob des übergebene Element ein Stereotyp ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Stereotyp ist
	 */
	public  boolean isStereotype(MBase m)  {
		return m!=null && m instanceof MStereotype;
	}

	/**
	 * Prüft, ob des übergebene Element ein Datentyp ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element ein Datentyp ist
	 */
	public  boolean isDataType(MBase m)  {
		return m!=null && m instanceof MDataType;
	}

	/**
	 * Prüft, ob des übergebene Element eine Assoziation ist
	 * @param m Zu prüfendes Element
	 * @return true, wenn das Element eine Assoziation ist
	 */
	public  boolean isAssociation(MBase m)  {
		return m!=null && m instanceof MAssociation;
	}

	public  MBase getType(MBase m)  {
		if (m==null) return null;
		if (m instanceof MStructuralFeature)  {
			return ((MStructuralFeature)m).getType();
		} else if (m instanceof MAssociationEnd)  {
			return ((MAssociationEnd)m).getType();
		} else if (m instanceof MParameter)  {
			return ((MParameter)m).getType();
		}
		return null;
	}


	public  String getTypeName(MBase m)  {
		return getName(getType(m));
	}


	public  String getFQTypeName(MBase m)  {
		return getFQName(getType(m));
	}

	public  int getVisibilityKind(MBase m)   {
		return (m!=null && m instanceof MModelElement) ? ((MModelElement)m).getVisibility().getValue() : -1;
	}

	public  String getVisibility(MBase m)  {
		int v = getVisibilityKind(m);
		switch (v)  {
			case MVisibilityKind._PRIVATE: return "private";
			case MVisibilityKind._PROTECTED: return "protected";
			case MVisibilityKind._PUBLIC: return "public";
			default: return "";
		}
	}

	public  boolean isAbstract(MBase m)  {
		return (m!=null && m instanceof MGeneralizableElement) ? ((MGeneralizableElement)m).isAbstract() : false;
	}

	public  boolean is(MBase m)  {
		return (m!=null && m instanceof MFeature) ? MScopeKind.CLASSIFIER==((MFeature)m).getOwnerScope() : false;
	}

	public  boolean isFinal(MBase m)  {
		String f = getTaggedValueAsString(m, "RationalRose$Java:Final");
		return f!=null && "true".equalsIgnoreCase(f);
	}

	public  Collection getModifiers(MBase m)  {
		Collection ret = new ArrayList();
		ret.add(getVisibility(m));
		if (isAbstract(m))  {
			ret.add("abstract");
		}
		if (is(m))  {
			ret.add("");
		}
		if (isFinal(m))  {
			ret.add("final");
		}

		return ret;
	}

	/**
	 * Eigenschaften der Zustandsübergänge
	 */
	public  MBase getTransitionSource(MBase m)  {
		return (m instanceof MTransition) ? ((MTransition)m).getSource() : null;
	}

	public  MBase getTransitionTarget(MBase m)  {
		return (m instanceof MTransition) ? ((MTransition)m).getTarget() : null;
	}

	public  MBase getTransitionGuard(MBase m)  {
		return (m instanceof MTransition) ? ((MTransition)m).getGuard() : null;
	}

	public  MBase getTransitionTrigger(MBase m)  {
		return (m instanceof MTransition) ? ((MTransition)m).getTrigger() : null;
	}

	public  MBase getTransitionEffect(MBase m)  {
		return (m instanceof MTransition) ? ((MTransition)m).getEffect() : null;
	}

	public  MBase getTransitionState(MBase m)  {
		return (m instanceof MTransition) ? ((MTransition)m).getState() : null;
	}

	/**
	 * Namen der Zustandsübergänge
	 */
	public  String getTransitionSourceName(MBase m)  {
		return (m instanceof MTransition) ? getName(((MTransition)m).getSource()) : null;
	}

	public  String getTransitionTargetName(MBase m)  {
		return (m instanceof MTransition) ? getName(((MTransition)m).getTarget()) : null;
	}

	public  String getTransitionGuardName(MBase m)  {
		return (m instanceof MTransition) ? getName(((MTransition)m).getGuard()) : null;
	}

	public  String getTransitionTriggerName(MBase m)  {
		return (m instanceof MTransition) ? getName(((MTransition)m).getTrigger()) : null;
	}

	public  String getTransitionEffectName(MBase m)  {
		return (m instanceof MTransition) ? getName(((MTransition)m).getEffect()) : null;
	}

	public  String getTransitionStateName(MBase m)  {
		return (m instanceof MTransition) ? getName(((MTransition)m).getState()) : null;
	}

	/**
	 * Verbindungen der Zustandübergänge
	 */
	public  Collection getIncomings(MBase m)  {
		if (m instanceof MStateVertex)  {
			Collection ret = new ArrayList();
			Collection c = ((MStateVertex)m).getIncomings();
			Iterator it = c.iterator();
			while (it.hasNext())  {
				MTransition ti = (MTransition)it.next();
				MStateVertex sv = ti.getSource();
				if (!isConcreteState(sv))  { // Synchronisationsbalken?
					return getIncomings(sv);
				} else  {
					ret.add(sv);
				}
			}
			return ret;
		}
		return Collections.EMPTY_LIST;
	}

	public  Collection getOutgoings(MBase m)  {
		if (m instanceof MStateVertex)  {
			Collection ret = new ArrayList();
			Collection c = ((MStateVertex)m).getOutgoings();
			Iterator it = c.iterator();
			while (it.hasNext())  {
				MTransition ti = (MTransition)it.next();
				MStateVertex sv = ti.getTarget();
				if (sv!=null)  {
					if (!isConcreteState(sv))  { // Synchronisationsbalken o. ä.?
						return getOutgoings(sv);
					} else  {
						ret.add(sv);
					}
				}
			}

			/*ret.add(((MStateVertex)m).getContainer());
			ret.addAll(getComponents(m));*/
			return ret;
		}
		return Collections.EMPTY_LIST;
	}

	public  Collection getOutgoingsTransitions(MBase m)  {
		if (m instanceof MStateVertex)  {
			return ((MStateVertex)m).getOutgoings();
		}
		return Collections.EMPTY_LIST;
	}

	public  Collection getAllOutgoings(MBase m)  {
		if (m instanceof MStateVertex)  {
			Collection ret = new ArrayList();
			Collection c = ((MStateVertex)m).getOutgoings();
			Iterator it = c.iterator();
			while (it.hasNext())  {
				MTransition ti = (MTransition)it.next();
				MStateVertex sv = ti.getTarget();
				if (sv!=null)  {					
					ret.add(sv);
				}
			}

			/*ret.add(((MStateVertex)m).getContainer());
			ret.addAll(getComponents(m));*/
			return ret;
		}
		return Collections.EMPTY_LIST;
	}
	public  boolean isConcreteState(MBase sv)  {
		return !(!isFinalState(sv) && sv instanceof MSynchState || isPseudostate(sv));
	}

	public  Collection getNextActions(MBase m)  {
		Collection outs = getOutgoings(m);
		return outs;
	}

	public  boolean isPersistent(MBase m)  {
		String value = getTaggedValueAsString(m, PERSISTENCE_TAG);
		return value==null || "false".equalsIgnoreCase(value);
	}

	public  MBase getOppositeEnd(MBase m)  {
		if (m!=null && m instanceof MAssociationEnd)  {
			return ((MAssociationEnd)m).getOppositeEnd();
		}
		return null;
	}

	/*
	 * Aktionen der Zustandsänderungen
	 */
	public  MBase getDoAction(MBase m)  {
		if (m!=null && m instanceof MState)  {
			return ((MState)m).getDoActivity();
		}
		return null;
	}

	public  MBase getExitAction(MBase m)  {
		if (m!=null && m instanceof MState)  {
			return ((MState)m).getExit();
		}
		return null;
	}

	public  MBase getEntryAction(MBase m)  {
		if (m!=null && m instanceof MState)  {
			return ((MState)m).getEntry();
		}
		return null;
	}

	public  Collection getActions(MBase m)  {
		if (m!=null && m instanceof MActionSequence)  {
			return ((MActionSequence)m).getActions();
		}
		return Collections.EMPTY_LIST;
	}

	public  Collection getTransitions(MBase m)  {
		if (m!=null && m instanceof MStateMachine)  {
			return ((MStateMachine)m).getTransitions();
		}
		return Collections.EMPTY_LIST;
	}


	/////////// Generalizations/Spezializations //////////////////
	public  Collection getGeneralizations(MBase m)  {
		return (m!=null && m instanceof MGeneralizableElement) ? ((MGeneralizableElement)m).getParents() : null;
	}


	public  Collection getSpecializations(MBase m)  {
		return (m!=null && m instanceof MGeneralizableElement) ? ((MGeneralizableElement)m).getChildren() : null;
	}

	/**
	 * Liefert die Superklasse für ein Element. Wenn das Element ein Interface ist, wird auch nach
	 * einem Interface gesucht, ansonsten nach einer Klasse.
	 */
	public  MBase getSuperclass(MBase m)  {
		//Collection c=filter(getGeneralizations(m), MClass.class);
		Collection c=null;
		if (m instanceof MClass)  {
			c=filter(getGeneralizations(m), MClass.class);
		} else  {
			c=filter(getGeneralizations(m), MInterface.class);
		}

		if (c==null || c.size()==0)  {
			return null;
		} else if (c.size()>1)  {
			EventService.fireInfo("Element "+getName(m)+" has more than one superclass!");
		}
		Iterator it = c.iterator();
		MBase ret=null;
		while (it.hasNext())  {
			if (ret==null)
				ret= (MBase)it.next();
			else it.next();	
		} // we must finish the iterator!
		return ret;
	}

	
	/**
	 * Liefert alle Superklassen für ein Element. Wenn das Element ein Interface ist, wird auch nach
	 * Interfaces gesucht, ansonsten nach Klassen. Diese Methode ist vorallem für Sprachen mit Mehrfachvererbung
	 * (z. B. C++) gedacht.
	 */
	public  Collection getAllSuperclasses(MBase m)  {
		//Collection c=filter(getGeneralizations(m), MClass.class);
		Collection c=null;
		if (m instanceof MClass)  {
			c=filter(getGeneralizations(m), MClass.class);
		} else  {
			c=filter(getGeneralizations(m), MInterface.class);
		}
		return c;
	}
	
	/**
	 * Liefert alle nicht-abstrakten Superklassen für ein Element. 
	 */
	public  Collection getConcreteSuperclasses(MBase m)  {
		//Collection c=filter(getGeneralizations(m), MClass.class);
		Collection c=null;
		if (m instanceof MClass)  {
			c=filter(getGeneralizations(m), MClass.class);
		} else  {
			c=filter(getGeneralizations(m), MInterface.class);
		}
		Collection result = new ArrayList();
		for(Iterator it = c.iterator(); it.hasNext();)  {
			MBase elem = (MBase)it.next();
			if (!isAbstract(elem))  {
				result.add(elem);
			}
		}
		return result;
	}

	/**
	 * Liefert alle nicht-abstrakten Superklassen für ein Element. 
	 */
	public  Collection getAbstractSuperclasses(MBase m)  {
		//Collection c=filter(getGeneralizations(m), MClass.class);
		Collection c=null;
		if (m instanceof MClass)  {
			c=filter(getGeneralizations(m), MClass.class);
		} else  {
			c=filter(getGeneralizations(m), MInterface.class);
		}
		Collection result = new ArrayList();
		for(Iterator it = c.iterator(); it.hasNext();)  {
			MBase elem = (MBase)it.next();
			if (isAbstract(elem))  {
				result.add(elem);
			}
		}
		return result;
	}
	
	
	private  String getPath(String fqName) {
		if (fqName==null) return null;

		int pos = fqName.lastIndexOf(".");
		if (pos!=-1) {
			return fqName.substring(0, pos);
		}
		return fqName;
	}


	/**
	 * Liefert alle Interfaces, die von einem Classifier realisiert werden
	 */
	public  Collection getInterfaces(MBase m)  {		
		Collection ret = new ArrayList();
		/* Für RR 2000e*/
		if (m instanceof MClassifier)  {			
			MClassifier cls = (MClassifier)m;
			
			Collection deps = filter(cls.getClientDependencies(), MAbstraction.class);
			Iterator it = deps.iterator();
			while (it.hasNext())  {
				MAbstraction abs = (MAbstraction)it.next();
				String st = getStereotype(abs);				
				if (st!=null && "realize".equalsIgnoreCase(st))  {
					Iterator supps = abs.getSuppliers().iterator();					
					while (supps.hasNext())  {			
						MBase ifc = (MBase)supps.next();			
						ret.add(ifc);
					}
				}
			}
		}
		return ret;		
		//return getAllSuperclasses(m);
	}
	
	public String getExpression(MBase m) {
		if (m instanceof MGuard) {
			MGuard g = (MGuard)m;			
			return g.getExpression()!=null ? g.getExpression().getBody() : "<none>";
		}
		return "invalid";
	}

	/////////// String utilities ////////////////
	public  String fu(String text)  {
		return (text!=null && text.length()>0) ? text.substring(0,1).toUpperCase()+text.substring(1,text.length()) : text;
	}

	public  String fd(String text)  {
		return (text!=null && text.length()>0) ? text.substring(0,1).toLowerCase()+text.substring(1,text.length()) : text;
	}
	
	public  String unqualify(String fqName, char pSep)  {
		if (fqName==null) return null;
		int pos = fqName.lastIndexOf(pSep);
		if (pos!=-1)  {
			return fqName.substring(pos+1);
		} else {
			return fqName;
		}
	}


	public  void dumpTV(MBase m)  {
		if (m==null) return;
		Collection c = getTaggedValues(m);
		Iterator it = c.iterator();
		//System.out.println("TV for "+m);
		while (it.hasNext())  {
			MTaggedValue tv = (MTaggedValue)it.next();
//			System.out.println("TV "+tv.getTag()+" = "+tv.getValue());
		}
	}

	public  String getAnsiUpperCase(MBase elem) {
		return getAnsi(elem).toUpperCase();
	}

	public  String getAnsiLowerCase(MBase elem) {
		return getAnsi(elem).toLowerCase();
	}

	/**
	 * Entfernt Sonderzeichen einschließlich Umlaute aus dem Namen des Elements
	 */
	public  String getAnsi(MBase elem) {
		if (elem==null) return "";

		String utf = MetaModel.getName(elem);
		//return "SCREEN_"+utf.hashCode();
		if (utf==null) return "";
		int n=utf.length();

		StringBuffer buf = new StringBuffer("");
		for (int i=0; i<n; i++) {
			char c = utf.charAt(i);
			if (Character.isLetterOrDigit(c)) {
			 	if (isUmlaut(c)) {
			 		buf.append(replaceUmlaut(c));
			 	} else {
					buf.append(c);
				}
			} else if (Character.isWhitespace(c)) {
				buf.append('_');
			}

		}
		return buf.toString().toUpperCase();
	}


	private  boolean isUmlaut(char c) {
		// MURKS: Unicodes verwenden!!
		return c=='ä' || c=='Ä' || c=='ü' || c=='Ü' || c=='ö' || c=='Ö' || c=='ß';
	}

	private  String replaceUmlaut(char c) {
		switch(c) {
			case 'ä' : return "ae";
			case 'Ä' : return "Ae";
			case 'ü' : return "ue";
			case 'Ü' : return "Ue";
			case 'ö' : return "oe";
			case 'Ö' : return "Oe";
			case 'ß' : return "ss";
			default:
				return ""+c;
		}
	}
	

%>